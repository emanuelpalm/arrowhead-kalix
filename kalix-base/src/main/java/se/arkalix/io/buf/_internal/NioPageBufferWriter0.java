package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader0;
import se.arkalix.io.buf.BufferWriter0;
import se.arkalix.io.mem.Read;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Internal
public class NioPageBufferWriter0 extends NioPageBufferModifier implements BufferWriter0 {
    public NioPageBufferWriter0(
        final ByteBufferPool pagePool,
        final ArrayList<ByteBuffer> pages,
        final boolean isExpanding
    ) {
        super(pagePool, pages, isExpanding);
    }

    @Override
    public BufferReader0 closeAndRead() {
        return closeAnd(NioPageBufferReader0::new);
    }

    @Override
    public int writeAt(final Read source, final int offset) {
        if (source == null) {
            throw new NullPointerException("source");
        }

        final var oldOffset = offset();
        offset(offset);
        try {
            return write(source);
        }
        finally {
            offset(oldOffset);
        }
    }
}
