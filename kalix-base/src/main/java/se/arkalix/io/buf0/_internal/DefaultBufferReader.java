package se.arkalix.io.buf0._internal;

import se.arkalix.io.buf0.Buffer;
import se.arkalix.io.buf0.BufferReader;
import se.arkalix.io.buf0.BufferReaderIsClosed;

public class DefaultBufferReader implements BufferReader {
    private final int length;

    private Buffer buffer;
    private int offset;

    public static DefaultBufferReader wrap(final byte[] byteArray) {
        return wrap(byteArray, 0, byteArray.length);
    }

    public static DefaultBufferReader wrap(final Buffer buffer) {
        return wrap(buffer, 0, buffer.length());
    }

    public static DefaultBufferReader wrap(final byte[] byteArray, final int offset, final int length) {
        return wrap(Buffer.wrap(byteArray), offset, length);
    }

    public static DefaultBufferReader wrap(final Buffer buffer, final int offset, final int length) {
        return new DefaultBufferReader(buffer, offset, length);
    }

    private DefaultBufferReader(final Buffer buffer, final int offset, final int length) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        if (offset < 0 || length < 0 || offset + length > buffer.length()) {
            throw new IndexOutOfBoundsException();
        }
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public void offset(final int offset) {
        if (offset < 0 || offset > length) {
            throw new IndexOutOfBoundsException();
        }
        this.offset = offset;
    }

    public void read(final byte[] destination, final int destinationOffset, final int length) {
        buffer.getAt(offset, destination, destinationOffset, length);
        offset += length;
    }

    public void read(final Buffer destination, final int destinationOffset, final int length) {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        if (destinationOffset + length > this.length || length > this.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.getAt(offset, destination, destinationOffset, length);
        offset += length;
    }

    public float readFloat() {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (Float.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        final var value = buffer.getFloatAt(offset);
        offset += Float.BYTES;
        return value;
    }

    public double readDouble() {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (Double.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        final var value = buffer.getDoubleAt(offset);
        offset += Double.BYTES;
        return value;
    }

    public byte readByte() {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (Byte.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        final var value = buffer.getByteAt(offset);
        offset += Byte.BYTES;
        return value;
    }

    public short readShort() {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (Short.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        final var value = buffer.getShortAt(offset);
        offset += Short.BYTES;
        return value;
    }

    public int readInt() {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (Integer.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        final var value = buffer.getIntAt(offset);
        offset += Integer.BYTES;
        return value;
    }

    public long readLong() {
        if (buffer == null) {
            throw new BufferReaderIsClosed();
        }
        if (Long.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        final var value = buffer.getLongAt(offset);
        offset += Long.BYTES;
        return value;
    }

    @Override
    public void close() {
        buffer = null;
    }
}
