package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer0;
import se.arkalix.io.buf.BufferReader0;
import se.arkalix.io.mem.Write;
import se.arkalix.util.annotation.Internal;

@Internal
public class EmptyBufferReader0 implements BufferReader0 {
    private static final EmptyBufferReader0 instance = new EmptyBufferReader0();

    public static EmptyBufferReader0 instance() {
        return instance;
    }

    @Override
    public void close() {
        // Does nothing.
    }

    @Override
    public Buffer0 copy() {
        return ByteArrayBuffer0.of(new byte[0]);
    }

    @Override
    public BufferReader0 dupe() {
        return this;
    }

    @Override
    public int limit() {
        return 0;
    }

    @Override
    public int offset() {
        return 0;
    }

    @Override
    public void offset(final int offset) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int readAt(final Write target, final int offset) {
        return 0;
    }
}
