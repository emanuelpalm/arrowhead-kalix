package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferAllocator;
import se.arkalix.util.annotation.Internal;

import java.util.Objects;

@Internal
public class DefaultBufferAllocator implements BufferAllocator {
    private final BufferPageAllocator bufferPageAllocator;

    public DefaultBufferAllocator(final BufferPageAllocator bufferPageAllocator) {
        this.bufferPageAllocator = Objects.requireNonNull(bufferPageAllocator, "fixedSizeBufferAllocator");
    }

    @Override
    public Buffer allocateDynamic() {
        return new PageBufferDynamic(bufferPageAllocator);
    }

    @Override
    public Buffer allocateFixed(final int fixedCapacity) {
        return new PageBufferFixed(
            bufferPageAllocator.allocateMemory(fixedCapacity),
            bufferPageAllocator.bufferCapacity()
        );
    }
}
