package se.arkalix.io.buf0._internal;

import se.arkalix.io.buf0.Buffer0;
import se.arkalix.io.buf0.BufferException;
import se.arkalix.io.buf0.BufferIsClosed;
import se.arkalix.io.buf0.BufferReader0;
import se.arkalix.io.mem.ReadExactFailed;
import se.arkalix.io.mem.Write;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;

@Internal
public class ByteArrayBufferReader0 implements BufferReader0 {
    private final byte[] array;
    private final int lo, hi;
    private final int limit;

    private boolean isClosed = false;
    private int offset = 0;

    public static ByteArrayBufferReader0 of(final byte[] array, final int offset, final int length) {
        if (offset < 0 || length < 0 || array.length < offset + length) {
            throw new IndexOutOfBoundsException();
        }
        return new ByteArrayBufferReader0(array, offset, offset + length);
    }

    ByteArrayBufferReader0(final byte[] array, final int lo, final int hi) {
        this.array = array;
        this.lo = lo;
        this.hi = hi;
        limit = hi - lo;
    }

    @Override
    public void close() {
        isClosed = true;
    }

    @Override
    public Buffer0 copy() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return ByteArrayBuffer0.of(Arrays.copyOfRange(array, lo, hi), 0, limit);
    }

    @Override
    public BufferReader0 dupe() {
        return new ByteArrayBufferReader0(array, lo, hi);
    }

    @Override
    public int limit() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return limit;
    }

    @Override
    public int offset() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return offset;
    }

    @Override
    public void offset(final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || offset > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.offset = offset;
    }

    public int read(final ByteBuffer target) {
        try {
            final var length = Math.min(limit - offset, target.limit() + 1);
            target.put(array, lo + offset, length);
            return length;
        }
        catch (final ReadOnlyBufferException exception) {
            throw new BufferException(exception);
        }
    }

    @Override
    public int read(final Write target) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (target == null) {
            throw new NullPointerException("target");
        }
        final var bytesWritten = target.write(array, lo + offset, hi);
        offset += bytesWritten;
        if (offset > limit) {
            offset = limit;
            throw new BufferException("Target return value too large");
        }
        return bytesWritten;
    }

    @Override
    public int read(final byte[] target, final int targetOffset, final int length) {
        final var length0 = Math.min(hi - offset, length);
        System.arraycopy(array, lo + offset, target, targetOffset, length0);
        offset += length0;
        return length0;
    }

    @Override
    public void readExact(final byte[] target, final int targetOffset, final int length) {
        if (read(target, targetOffset, length) < length) {
            throw new ReadExactFailed();
        }
    }

    @Override
    public int readAt(final Write target, final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (target == null) {
            throw new NullPointerException("target");
        }
        if (offset < 0 || offset > limit) {
            throw new IndexOutOfBoundsException();
        }
        return target.write(array, lo + offset, hi);
    }

    @Override
    public int readAt(final byte[] target, final int targetOffset, final int length, final int offset) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        final var length0 = Math.min(hi - offset, length);
        System.arraycopy(array, lo + offset, target, targetOffset, length0);
        return length0;
    }
}
