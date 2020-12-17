package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer0;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.buf.BufferReader0;
import se.arkalix.io.buf.BufferWriter0;
import se.arkalix.util.annotation.Internal;

import java.util.Arrays;
import java.util.Objects;

@Internal
public class ByteArrayBuffer0 implements Buffer0 {
    private final byte[] array;
    private final int lo, hi;

    private boolean isClosed = false;

    public static ByteArrayBuffer0 of(final byte[] array) {
        return new ByteArrayBuffer0(array, 0, array.length);
    }

    public static ByteArrayBuffer0 of(final byte[] array, final int offset, final int length) {
        return new ByteArrayBuffer0(array, offset, length);
    }

    private ByteArrayBuffer0(final byte[] array, final int offset, final int length) {
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
    public BufferReader0 closeAndRead() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        return null;
    }

    @Override
    public BufferWriter0 closeAndWrite() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        return new ByteArrayBufferWriter0(array, lo, hi);
    }

    @Override
    public Buffer0 copy() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return of(Arrays.copyOfRange(array, lo, hi));
    }
}
