package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.io.mem.Read;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Internal
public class NioPageBufferWriter extends NioPageBufferModifier implements BufferWriter {
    public NioPageBufferWriter(
        final NioPagePool pagePool,
        final ArrayList<ByteBuffer> pages,
        final boolean isExpanding
    ) {
        super(pagePool, pages, isExpanding);
    }

    @Override
    public BufferReader closeAndRead() {
        return closeAnd(NioPageBufferReader::new);
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
