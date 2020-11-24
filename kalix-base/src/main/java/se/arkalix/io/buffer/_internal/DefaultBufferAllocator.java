package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferAllocator;

import java.nio.ByteBuffer;
import java.util.Objects;

public class DefaultBufferAllocator implements BufferAllocator {
    private final FixedSizeBufferAllocator fixedSizeBufferAllocator;

    public DefaultBufferAllocator(final FixedSizeBufferAllocator fixedSizeBufferAllocator) {
        this.fixedSizeBufferAllocator = Objects.requireNonNull(fixedSizeBufferAllocator, "fixedSizeBufferAllocator");
    }

    @Override
    public Buffer allocate() {
        return new ExpandingBuffer(fixedSizeBufferAllocator);
    }

    @Override
    public Buffer allocateWithInitialCapacity(final int initialCapacity) {
        final var buffer = new ExpandingBuffer(fixedSizeBufferAllocator);
        buffer.capacity(initialCapacity);
        return buffer;
    }

    @Override
    public Buffer allocateWithFixedCapacity(final int fixedCapacity) {
        // TODO: Use non-expanding composite buffer with fixed size buffers to avoid not using pooled memory.
        return new NioBuffer(() -> {}, ByteBuffer.allocateDirect(fixedCapacity));
    }
}
