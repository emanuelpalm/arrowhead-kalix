package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.mem.Write;
import se.arkalix.util.annotation.Internal;

@Internal
public class EmptyBufferReader implements BufferReader {
    private static final EmptyBufferReader instance = new EmptyBufferReader();

    public static EmptyBufferReader instance() {
        return instance;
    }

    @Override
    public void close() {
        // Does nothing.
    }

    @Override
    public Buffer copy() {
        return ByteArrayBuffer.of(new byte[0]);
    }

    @Override
    public BufferReader dupe() {
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
