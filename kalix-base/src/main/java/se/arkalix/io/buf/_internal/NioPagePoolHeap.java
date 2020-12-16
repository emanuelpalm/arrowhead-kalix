package se.arkalix.io.buf._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Internal
public class NioPagePoolHeap extends NioPagePool {
    @Override
    protected ByteBuffer createBufferOfSize(final int sizeInBytes) {
        final var byteBuffer = ByteBuffer.allocate(sizeInBytes);
        byteBuffer.order(ByteOrder.nativeOrder());
        return byteBuffer;
    }
}
