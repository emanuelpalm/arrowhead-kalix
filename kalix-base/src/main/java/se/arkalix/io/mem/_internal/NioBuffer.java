package se.arkalix.io.mem._internal;

import se.arkalix.io.mem.Buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class NioBuffer implements Buffer {
    private final ByteBuffer byteBuffer;

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
        final var byteBuffer = ByteBuffer.wrap(byteArray, offset, length);
        byteBuffer.order(ByteOrder.nativeOrder());
        return new NioBuffer(byteBuffer);
    }

    private NioBuffer(final ByteBuffer byteBuffer) {
        this.byteBuffer = Objects.requireNonNull(byteBuffer, "byteBuffer");
    }

    @Override
    public int length() {
        return byteBuffer.limit();
    }

    @Override
    public float getFloatAt(final int position) {
        return byteBuffer.getFloat(position);
    }

    @Override
    public double getDoubleAt(final int position) {
        return byteBuffer.getDouble(position);
    }

    @Override
    public byte getByteAt(final int position) {
        return byteBuffer.get(position);
    }

    @Override
    public short getShortAt(final int position) {
        return byteBuffer.getShort(position);
    }

    @Override
    public int getIntAt(final int position) {
        return byteBuffer.getInt(position);
    }

    @Override
    public long getLongAt(final int position) {
        return byteBuffer.getLong(position);
    }

    @Override
    public void putAt(final int position, final Buffer source, final int sourcePosition, int length) {
        byteBuffer.position(position);

        if (source instanceof NioBuffer) {
            final var sourceByteBuffer = ((NioBuffer) source).byteBuffer;
            sourceByteBuffer.position(sourcePosition);
            sourceByteBuffer.limit(sourcePosition + length);

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
    public void putByteAt(final int position, final byte source) {
        byteBuffer.put(position, source);
    }

    @Override
    public void putShortAt(final int position, final short source) {
        byteBuffer.putShort(position, source);
    }

    @Override
    public void putIntAt(final int position, final int source) {
        byteBuffer.putInt(position, source);
    }

    @Override
    public void putLongAt(final int position, final long source) {
        byteBuffer.putLong(position, source);
    }
}
