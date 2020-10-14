package se.arkalix.internal.net.http.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import se.arkalix.ArSystem;
import se.arkalix.descriptor.EncodingDescriptor;
import se.arkalix.internal.net.NettyBodyOutgoing;
import se.arkalix.internal.net.NettySimpleChannelInboundHandler;
import se.arkalix.net.http.HttpConnectionWithArSystem;
import se.arkalix.net.http.service.HttpServiceWsConnection;
import se.arkalix.net.http.service.HttpWsOutgoingMessage;
import se.arkalix.security.identity.SystemIdentity;
import se.arkalix.util.InternalException;
import se.arkalix.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static se.arkalix.internal.util.concurrent.NettyFutures.adapt;

public class NettyHttpServiceWsConnection
    extends NettySimpleChannelInboundHandler<WebSocketFrame>
    implements HttpServiceWsConnection
{
    private Channel channel;
    private HttpConnectionWithArSystem inner;

    private EncodingDescriptor defaultEncoding;

    private NettyHttpWsIncomingMessage incoming = null;

    void defaultEncoding(final EncodingDescriptor defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        inner = ctx.pipeline().get(NettyHttpServiceConnection.class);
        if (inner == null) {
            throw new InternalException("No NettyHttpServiceConnection instance in Netty pipeline");
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame msg) throws Exception {
        if (incoming == null) {
            incoming = new NettyHttpWsIncomingMessage(ctx.alloc(), msg.content().readableBytes());
            // TODO: Pass incoming message to WS route handle.
        }
        incoming.append(msg);
        if (msg.isFinalFragment()) {
            incoming.finish();
            incoming = null;
        }
    }

    @Override
    public Future<?> send(final HttpWsOutgoingMessage message) {
        try {
            final var body = message.body()
                .orElseThrow(() -> new IllegalArgumentException("Cannot " +
                    "send WebSocket message without body"));

            if (body instanceof String && message.charset()
                .map(charset -> charset == StandardCharsets.UTF_8)
                .orElse(false))
            {
                return adapt(channel.write(new TextWebSocketFrame((String) body)));
            }

            final var outgoing = NettyBodyOutgoing.from(message, channel.alloc(), defaultEncoding);

            final var byteBuf = outgoing.asByteBuf().orElse(null);
            if (byteBuf != null) {
                return adapt(channel.write(new BinaryWebSocketFrame(byteBuf)));
            }

            final var fileRegion = outgoing.asFileRegion().orElse(null);
            if (fileRegion != null) {
                // TODO: Will this even work?
                return adapt(channel.write(fileRegion));
            }

            return Future.failure(new UnsupportedOperationException("Cannot " +
                "send WebSocket message bodies of type " + body.getClass()));
        }
        catch (final Throwable throwable) {
            return Future.failure(throwable);
        }
    }

    @Override
    public SystemIdentity remoteIdentity() {
        return inner.remoteIdentity();
    }

    @Override
    public ArSystem localSystem() {
        return inner.localSystem();
    }

    @Override
    public InetSocketAddress remoteSocketAddress() {
        return inner.remoteSocketAddress();
    }

    @Override
    public InetSocketAddress localSocketAddress() {
        return inner.localSocketAddress();
    }

    @Override
    public boolean isLive() {
        return inner.isLive();
    }

    @Override
    public boolean isSecure() {
        return inner.isSecure();
    }

    @Override
    public Future<?> close() {
        return inner.close();
    }
}
