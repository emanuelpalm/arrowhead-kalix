package se.arkalix.io.buf._internal;

import java.nio.ByteBuffer;

public class Page {
    final ByteBuffer buffer;
    final int startOffset;
    final int stopOffset;

    public Page(final ByteBuffer buffer, final int startOffset, final int stopOffset) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        if (startOffset < 0 || startOffset < stopOffset || stopOffset > buffer.capacity()) {
            throw new IndexOutOfBoundsException();
        }
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.stopOffset = stopOffset;
    }

    public int size() {
        return stopOffset - startOffset;
    }
}
