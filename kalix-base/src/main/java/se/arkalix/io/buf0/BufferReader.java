package se.arkalix.io.buf0;

import java.nio.ByteOrder;

public interface BufferReader extends AutoCloseable {
    int length();

    int offset();

    void offset(int offset);

    default void read(final byte[] destination) {
        read(destination, 0, destination.length);
    }

    default void read(final Buffer destination) {
        read(destination, 0, destination.length());
    }

    void read(final byte[] destination, final int destinationOffset, final int length);

    void read(final Buffer destination, final int destinationOffset, final int length);

    float readFloat();

    double readDouble();

    byte readByte();

    short readShort();

    default short readShortBe() {
        var value = readShort();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default short readShortLe() {
        var value = readShort();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    int readInt();

    default int readIntBe() {
        var value = readInt();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default int readIntLe() {
        var value = readInt();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    long readLong();

    default long readLongBe() {
        var value = readLong();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default long readLongLe() {
        var value = readLong();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    @Override
    void close();
}
