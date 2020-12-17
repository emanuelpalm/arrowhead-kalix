package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.io.buf.BufferWriterIsClosed;

public class DefaultBufferWriter implements BufferWriter {
    private final int length;

    private Buffer buffer;
    private int offset;

    public static DefaultBufferWriter wrap(final byte[] byteArray) {
        return wrap(byteArray, 0, byteArray.length);
    }

    public static DefaultBufferWriter wrap(final Buffer buffer) {
        return wrap(buffer, 0, buffer.length());
    }

    public static DefaultBufferWriter wrap(final byte[] byteArray, final int offset, final int length) {
        return wrap(Buffer.wrap(byteArray), offset, length);
    }

    public static DefaultBufferWriter wrap(final Buffer buffer, final int offset, final int length) {
        return new DefaultBufferWriter(buffer, offset, length);
    }

    private DefaultBufferWriter(final Buffer buffer, final int offset, final int length) {
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

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (sourceOffset + length > this.length || length > this.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putAt(offset, source, sourceOffset, length);
        offset += length;
    }

    public void write(final Buffer source, final int sourceOffset, final int length) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (sourceOffset + length > this.length || length > this.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putAt(offset, source, sourceOffset, length);
        offset += length;
    }

    public void writeFloat(final float value) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (Float.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putFloatAt(offset, value);
        offset += Float.BYTES;
    }

    public void writeDouble(final double value) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (Double.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putDoubleAt(offset, value);
        offset += Double.BYTES;
    }

    public void writeByte(final byte value) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (Byte.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putByteAt(offset, value);
        offset += Byte.BYTES;
    }

    public void writeShort(final short value) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (Short.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putShortAt(offset, value);
        offset += Short.BYTES;
    }

    public void writeInt(final int value) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (Integer.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putIntAt(offset, value);
        offset += Integer.BYTES;
    }

    public void writeLong(final long value) {
        if (buffer == null) {
            throw new BufferWriterIsClosed();
        }
        if (Long.BYTES > length - offset) {
            throw new IndexOutOfBoundsException();
        }
        buffer.putLongAt(offset, value);
        offset += Long.BYTES;
    }

    @Override
    public void close() {
        buffer = null;
    }
}
