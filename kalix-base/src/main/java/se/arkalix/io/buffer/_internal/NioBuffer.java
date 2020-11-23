package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferCapacityNotIncreased;
import se.arkalix.io.buffer.BufferIsClosed;
import se.arkalix.io.buffer.BufferView;

import java.nio.ByteBuffer;

public class NioBuffer implements Buffer {
    private ByteBuffer inner;

    public NioBuffer(final ByteBuffer inner) {
        this.inner = inner;
    }

    @Override
    public int capacity() {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        return inner.capacity();
    }

    @Override
    public void capacity(final int capacity) {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        if (capacity > inner.capacity()) {
            throw new BufferCapacityNotIncreased();
        }
        inner.limit(capacity);
    }

    @Override
    public void drop() {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        inner = null;
    }

    @Override
    public int offset() {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        return inner.position();
    }

    @Override
    public void offset(final int offset) {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > inner.limit()) {
            throw new BufferCapacityNotIncreased();
        }
        inner.position(offset);
    }

    @Override
    public void putByte(final int offset, final byte b) {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > inner.limit()) {
            throw new BufferCapacityNotIncreased();
        }
        inner.put(offset, b);
    }

    @Override
    public void putBytes(int offset, final byte[] source, int sourceOffset, final int length) {
        if (inner == null) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || sourceOffset < 0 || length < 0 || ) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > inner.limit()) {
            throw new BufferCapacityNotIncreased();
        }
        while (sourceOffset < length) {
            inner.put(offset++, source[sourceOffset++]);
        }
    }

    @Override
    public BufferView view() {
        return null;
    }

    @Override
    public void writeByte(final byte b) {

    }

    @Override
    public void writeBytes(final byte[] source, final int sourceOffset, final int length) {

    }
}
