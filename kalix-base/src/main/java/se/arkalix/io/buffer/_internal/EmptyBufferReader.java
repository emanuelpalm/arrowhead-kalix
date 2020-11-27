package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.BufferReader;
import se.arkalix.util.annotation.Internal;

@Internal
public class EmptyBufferReader implements BufferReader {
    private static final EmptyBufferReader instance = new EmptyBufferReader();

    public static EmptyBufferReader instance() {
        return instance;
    }

    private EmptyBufferReader() {}

    @Override
    public void close() {}

    @Override
    public BufferReader dupe() {
        return this;
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
    public byte getByte(final int offset) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void getBytes(final int offset, final byte[] target, final int targetOffset, final int length) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return 0;
    }
}
