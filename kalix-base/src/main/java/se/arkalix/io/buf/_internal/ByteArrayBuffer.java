package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.util.Arrays;
import java.util.Objects;

@Internal
public class ByteArrayBuffer implements Buffer {
    private final byte[] array;
    private final int lo, hi;

    private boolean isClosed = false;

    public static ByteArrayBuffer of(final byte[] array) {
        return new ByteArrayBuffer(array, 0, array.length);
    }

    public static ByteArrayBuffer of(final byte[] array, final int offset, final int length) {
        return new ByteArrayBuffer(array, offset, length);
    }

    private ByteArrayBuffer(final byte[] array, final int offset, final int length) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        final var hi = offset + length;
        if (offset < 0 || length < 0 || hi > array.length) {
            throw new IndexOutOfBoundsException();
        }
        this.array = Objects.requireNonNull(array);
        this.lo = offset;
        this.hi = hi;
    }

    @Override
    public void close() {
        isClosed = true;
    }

    @Override
    public BufferReader closeAndRead() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        return null;
    }

    @Override
    public BufferWriter closeAndWrite() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        return new ByteArrayBufferWriter(array, lo, hi);
    }

    @Override
    public Buffer copy() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return of(Arrays.copyOfRange(array, lo, hi));
    }
}
