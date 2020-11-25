package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferAllocator;
import se.arkalix.util.annotation.Internal;

import java.util.Objects;

@Internal
public class DefaultBufferAllocator implements BufferAllocator {
    private final PageAllocator pageAllocator;

    public DefaultBufferAllocator(final PageAllocator pageAllocator) {
        this.pageAllocator = Objects.requireNonNull(pageAllocator, "pageAllocator");
    }

    @Override
    public Buffer allocateDynamic() {
        return new PageBufferDynamic(pageAllocator);
    }

    @Override
    public Buffer allocateFixed(final int fixedCapacity) {
        return new PageBufferFixed(
            pageAllocator.allocateBytes(fixedCapacity),
            pageAllocator.pageSize()
        );
    }
}
