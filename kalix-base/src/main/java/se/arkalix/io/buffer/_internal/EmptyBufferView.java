package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.BufferView;
import se.arkalix.util.annotation.Internal;

@Internal
public class EmptyBufferView implements BufferView {
    private static final EmptyBufferView instance = new EmptyBufferView();

    public static EmptyBufferView instance() {
        return instance;
    }

    private EmptyBufferView() {}

    @Override
    public void close() {}

    @Override
    public BufferView dupe() {
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
    public byte readByte() {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void readBytes(final byte[] target, final int targetOffset, final int length) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
        return 0;
    }
}
