package se.arkalix.internal.net.http.service;

import io.netty.buffer.ByteBufAllocator;
import se.arkalix.descriptor.EncodingDescriptor;
import se.arkalix.internal.net.NettyMessageIncoming;

import java.nio.charset.Charset;
import java.util.Optional;

public class NettyHttpWsIncomingMessage extends NettyMessageIncoming {
    public NettyHttpWsIncomingMessage(final ByteBufAllocator alloc, final int expectedBodyLength) {
        super(alloc, expectedBodyLength);
    }

    @Override
    public Optional<Charset> charset() {
        return Optional.empty();
    }

    @Override
    public Optional<EncodingDescriptor> encoding() {
        return Optional.empty();
    }
}
