package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class NioBuffer implements Buffer {
    private ByteBuffer byteBuffer;

    public static NioBuffer allocate(final int capacity) {
        final var byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.order(ByteOrder.nativeOrder());
        return new NioBuffer(byteBuffer);
    }

    public static NioBuffer allocateDirect(final int capacity) {
        final var byteBuffer = ByteBuffer.allocateDirect(capacity);
        byteBuffer.order(ByteOrder.nativeOrder());
        return new NioBuffer(byteBuffer);
    }

    public static NioBuffer wrap(final byte[] byteArray, final int offset, final int length) {
        if (byteArray == null) {
            throw new NullPointerException("byteArray");
        }
        final var byteBuffer = ByteBuffer.wrap(byteArray, offset, length);
        byteBuffer.order(ByteOrder.nativeOrder());
        return new NioBuffer(byteBuffer);
    }

    private NioBuffer(final ByteBuffer byteBuffer) {
        this.byteBuffer = Objects.requireNonNull(byteBuffer, "byteBuffer");
    }

    @Override
    public int length() {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.limit();
    }

    @Override
    public void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || offset > byteBuffer.limit()) {
            throw new IndexOutOfBoundsException();
        }
        byteBuffer.position(offset);
        byteBuffer.get(destination, destinationOffset, length);
    }

    @Override
    public float getFloatAt(final int position) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.getFloat(position);
    }

    @Override
    public double getDoubleAt(final int position) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.getDouble(position);
    }

    @Override
    public byte getByteAt(final int position) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.get(position);
    }

    @Override
    public short getShortAt(final int position) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.getShort(position);
    }

    @Override
    public int getIntAt(final int position) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.getInt(position);
    }

    @Override
    public long getLongAt(final int position) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        return byteBuffer.getLong(position);
    }

    @Override
    public void putAt(final int offset, final byte[] source, final int sourceOffset, final int length) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || offset > byteBuffer.limit()) {
            throw new IndexOutOfBoundsException();
        }
        byteBuffer.position(offset);
        byteBuffer.put(source, sourceOffset, length);
    }

    @Override
    public void putAt(final int offset, final Buffer source, final int sourceOffset, int length) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || offset > byteBuffer.limit()) {
            throw new IndexOutOfBoundsException();
        }
        byteBuffer.position(offset);

        if (source instanceof NioBuffer) {
            final var sourceByteBuffer = ((NioBuffer) source).byteBuffer;
            sourceByteBuffer.position(sourceOffset);
            sourceByteBuffer.limit(sourceOffset + length);

            byteBuffer.put(sourceByteBuffer);
            return;
        }

        // Use intermediate buffer as a last resort.
        final var byteArray = new byte[2048];

        while (length > byteArray.length) {
            source.getAt(0, byteArray, 0, byteArray.length);
            byteBuffer.put(byteArray);
            length -= byteArray.length;
        }

        source.getAt(0, byteArray, 0, length);
        byteBuffer.put(byteArray, 0, length);
    }

    @Override
    public void putFloatAt(final int position, final float value) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        byteBuffer.putFloat(position, value);
    }

    @Override
    public void putDoubleAt(final int position, final double value) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        byteBuffer.putDouble(position, value);
    }

    @Override
    public void putByteAt(final int position, final byte source) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        byteBuffer.put(position, source);
    }

    @Override
    public void putShortAt(final int position, final short source) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        byteBuffer.putShort(position, source);
    }

    @Override
    public void putIntAt(final int position, final int source) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        byteBuffer.putInt(position, source);
    }

    @Override
    public void putLongAt(final int position, final long source) {
        if (byteBuffer == null) {
            throw new BufferIsClosed();
        }
        byteBuffer.putLong(position, source);
    }

    @Override
    public void close() {
        byteBuffer = null;
    }
}
