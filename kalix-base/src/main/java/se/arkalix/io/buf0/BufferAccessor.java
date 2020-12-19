package se.arkalix.io.buf0;

import java.nio.ByteOrder;

public interface BufferAccessor extends AutoCloseable {
    int length();

    default void getAt(final int offset, final byte[] destination) {
        getAt(offset, destination, 0, destination.length);
    }

    default void getAt(final int offset, final Buffer destination) {
        getAt(offset, destination, 0, destination.length());
    }

    void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length);

    void getAt(final int offset, final Buffer destination, final int destinationOffset, final int length);

    float getFloatAt(final int offset);

    double getDoubleAt(final int offset);

    byte getByteAt(final int offset);

    short getShortAt(final int offset);

    default short getShortBeAt(final int offset) {
        var value = getShortAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default short getShortLeAt(final int offset) {
        var value = getShortAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    int getIntAt(final int offset);

    default int getIntBeAt(final int offset) {
        var value = getIntAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default int getIntLeAt(final int offset) {
        var value = getIntAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    long getLongAt(final int offset);

    default long getLongBeAt(final int offset) {
        var value = getLongAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default long getLongLeAt(final int offset) {
        var value = getLongAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    @Override
    void close();
}
