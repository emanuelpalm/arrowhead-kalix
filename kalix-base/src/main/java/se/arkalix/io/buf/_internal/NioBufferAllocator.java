package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferAllocator;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

@Internal
public class NioBufferAllocator implements BufferAllocator {
    private final NioPagePool pagePool;

    public static NioBufferAllocator createDirectAllocator() {
        return new NioBufferAllocator(new NioPagePoolDirect());
    }

    public static NioBufferAllocator createHeapAllocator() {
        return new NioBufferAllocator(new NioPagePoolHeap());
    }

    private NioBufferAllocator(final NioPagePool pagePool) {
        this.pagePool = Objects.requireNonNull(pagePool, "pagePool");
    }

    @Override
    public Buffer allocate(final int capacity) {
        final var pages = new ArrayList<ByteBuffer>();
        pagePool.allocate(pages, capacity);
        return new NioPageBuffer(pagePool, pages, false);
    }

    @Override
    public Buffer allocateDynamic(final int initialCapacity) {
        final var pages = new ArrayList<ByteBuffer>();
        pagePool.allocate(pages, initialCapacity);
        return new NioPageBuffer(pagePool, pages, true);
    }
}
