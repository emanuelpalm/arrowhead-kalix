package se.arkalix.io.buf0;

import java.nio.ByteOrder;

public interface BufferWriter extends AutoCloseable {
    int length();

    int offset();

    void offset(int offset);

    default void write(final byte[] source) {
        write(source, 0, source.length);
    }

    default void write(final BufferAccessor source) {
        write(source, 0, source.length());
    }

    void write(final byte[] source, final int sourceOffset, final int length);

    void write(final BufferAccessor source, final int sourceOffset, final int length);

    void writeFloat(final float value);

    void writeDouble(final double value);

    void writeByte(final byte value);

    void writeShort(final short value);

    default void writeShortBe(short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        writeShort(value);
    }

    default void writeShortLe(short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        writeShort(value);
    }

    void writeInt(final int value);

    default void writeIntBe(int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        writeInt(value);
    }

    default void writeIntLe(int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        writeInt(value);
    }

    void writeLong(final long value);

    default void writeLongBe(long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        writeLong(value);
    }

    default void writeLongLe(long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        writeLong(value);
    }

    @Override
    void close();
}
