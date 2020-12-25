package se.arkalix.io.buf._internal;

import java.nio.ByteBuffer;

public class Page {
    private final ByteBuffer byteBuffer;
    private final int startOffset;
    private final int stopOffset;

    public Page(final ByteBuffer byteBuffer, final int startOffset, final int stopOffset) {
        if (byteBuffer == null) {
            throw new NullPointerException("buffer");
        }
        if (startOffset < 0 || startOffset < stopOffset || stopOffset > byteBuffer.capacity()) {
            throw new IndexOutOfBoundsException();
        }
        this.byteBuffer = byteBuffer;
        this.startOffset = startOffset;
        this.stopOffset = stopOffset;
    }

    public int size() {
        return stopOffset - startOffset;
    }

    public ByteBuffer asByteBufferPositionedAt(final int offset) {
        return byteBuffer
            .position(startOffset + offset)
            .limit(stopOffset);
    }
}
