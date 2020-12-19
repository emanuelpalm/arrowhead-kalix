package se.arkalix.io.buf0._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Internal
public class ByteBufferPoolDirect extends ByteBufferPool {
    @Override
    protected ByteBuffer createBufferOfSize(final int sizeInBytes) {
        final var byteBuffer = ByteBuffer.allocateDirect(sizeInBytes);
        byteBuffer.order(ByteOrder.nativeOrder());
        return byteBuffer;
    }
}
