package se.arkalix.io.net._internal;

import se.arkalix.io.IoException;
import se.arkalix.io.evt._internal.NioEventLoop;
import se.arkalix.io.net.ListenerIsClosed;
import se.arkalix.io.net.TcpListener;
import se.arkalix.io.net.TcpSocket;
import se.arkalix.util.annotation.Internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;
import java.util.concurrent.Flow;

@Internal
public class NioTcpListener implements TcpListener {
    private final NioEventLoop eventLoop;
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public NioTcpListener(
        final NioEventLoop eventLoop,
        final Selector selector,
        final ServerSocketChannel serverSocketChannel
    ) {
        this.eventLoop = Objects.requireNonNull(eventLoop, "eventLoop");
        this.selector = Objects.requireNonNull(selector, "selector");
        this.serverSocketChannel = Objects.requireNonNull(serverSocketChannel, "serverSocketChannel");
    }

    @Override
    public InetSocketAddress localSocketAddress() {
        try {
            return (InetSocketAddress) serverSocketChannel.getLocalAddress();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }

    @Override
    public void close() {
        try {
            serverSocketChannel.close();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super TcpSocket> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("subscriber");
        }
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, (Runnable) () -> {
                final var bufferAllocator = eventLoop.bufferAllocator();
                while (true) {
                    try {
                        final var socketChannel = serverSocketChannel.accept();
                        if (socketChannel == null) {
                            break;
                        }
                        eventLoop.nextThread()
                            .enqueue(selector -> subscriber.onNext(new NioTcpSocket(
                                bufferAllocator, selector, socketChannel)));
                    }
                    catch (final IOException exception) {
                        subscriber.onError(new IoException(exception));
                    }
                }
            });
        }
        catch (final ClosedChannelException exception) {
            subscriber.onError(new ListenerIsClosed(exception));
        }
    }
}
