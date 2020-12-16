package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.*;
import se.arkalix.io.mem.Read;
import se.arkalix.io.mem.WriteAllFailed;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;

@Internal
public class ByteArrayBufferWriter implements BufferWriter {
    private final byte[] array;
    private final int lo, hi;
    private final int limit;

    private boolean isClosed = false;
    private int offset = 0;

    public static ByteArrayBufferWriter of(final byte[] array, final int offset, final int length) {
        if (offset < 0 || length < 0 || array.length < offset + length) {
            throw new IndexOutOfBoundsException();
        }
        return new ByteArrayBufferWriter(array, offset, offset + length);
    }

    ByteArrayBufferWriter(final byte[] array, final int lo, final int hi) {
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
    public BufferReader closeAndRead() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        return new ByteArrayBufferReader(array, lo, hi);
    }

    @Override
    public Buffer copy() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return ByteArrayBuffer.of(Arrays.copyOfRange(array, lo, hi), 0, limit);
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

    public int write(final ByteBuffer source) {
        try {
            final var length = Math.min(limit - offset, source.limit() + 1);
            source.get(array, lo + offset, length);
            return length;
        }
        catch (final ReadOnlyBufferException exception) {
            throw new BufferException(exception);
        }
    }

    @Override
    public int write(final Read source) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
        final var bytesRead = source.read(array, lo + offset, hi);
        offset += bytesRead;
        if (offset > limit) {
            offset = limit;
            throw new BufferException("Source return value too large");
        }
        return bytesRead;
    }

    @Override
    public int write(final byte[] source, final int sourceOffset, final int length) {
        final var length0 = Math.min(hi - offset, length);
        System.arraycopy(source, sourceOffset, array, lo + offset, length0);
        offset += length0;
        return length0;
    }

    @Override
    public void writeAll(final byte[] source, final int sourceOffset, final int length) {
        if (write(source, sourceOffset, length) < length) {
            throw new WriteAllFailed();
        }
    }

    @Override
    public int writeAt(final Read source, final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (offset < 0 || offset > limit) {
            throw new IndexOutOfBoundsException();
        }
        return source.read(array, lo + offset, hi);
    }

    @Override
    public int writeAt(final byte[] source, final int sourceOffset, final int length, final int offset) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        final var length0 = Math.min(hi - offset, length);
        System.arraycopy(source, sourceOffset, array, lo + offset, length0);
        return length0;
    }
}
