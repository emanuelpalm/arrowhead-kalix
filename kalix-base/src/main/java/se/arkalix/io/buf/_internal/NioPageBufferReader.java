package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.mem.Write;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Internal
public class NioPageBufferReader extends NioPageBufferModifier implements BufferReader {
    private final AtomicLong referenceCount;

    public NioPageBufferReader(final NioPagePool pagePool, final ArrayList<ByteBuffer> pages) {
        super(pagePool, pages, false);
        referenceCount = new AtomicLong(1);
    }

    private NioPageBufferReader(
        final NioPagePool pagePool,
        final ArrayList<ByteBuffer> pages,
        final AtomicLong referenceCount
    ) {
        super(pagePool, pages, false);
        this.referenceCount = referenceCount;

        referenceCount.getAndIncrement();
    }

    @Override
    public void close() {
        if (!isClosed() && referenceCount.decrementAndGet() == 0) {
            super.close();
        }
    }

    @Override
    public BufferReader dupe() {
        ensureIsOpen();

        return new NioPageBufferReader(pagePoolUnsafe(), duplicatePagesUnsafe(), referenceCount);
    }

    @Override
    public int readAt(final Write target, final int offset) {
        if (target == null) {
            throw new NullPointerException("target");
        }

        final var oldOffset = offset();
        offset(offset);
        try {
            return target.write(this);
        }
        finally {
            offset(oldOffset);
        }
    }
}
