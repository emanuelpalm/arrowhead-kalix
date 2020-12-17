package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.NioBuffer;

import java.nio.ByteOrder;

public interface Buffer extends AutoCloseable {
    static Buffer wrap(final byte[] byteArray) {
        return NioBuffer.wrap(byteArray, 0, byteArray.length);
    }

    static Buffer wrap(final byte[] byteArray, final int offset, final int length) {
        return NioBuffer.wrap(byteArray, offset, length);
    }

    int length();

    default void getAt(final int offset, final byte[] destination) {
        getAt(offset, destination, 0, destination.length);
    }

    default void getAt(final int offset, final Buffer destination) {
        getAt(offset, destination, 0, destination.length());
    }

    default void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        getAt(offset, wrap(destination), destinationOffset, length);
    }

    default void getAt(final int offset, final Buffer destination, final int destinationOffset, final int length) {
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        destination.putAt(destinationOffset, this, offset, length);
    }

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

    default void putAt(final int offset, final byte[] source) {
        putAt(offset, source, 0, source.length);
    }

    default void putAt(final int offset, final Buffer source) {
        putAt(offset, source, 0, source.length());
    }

    default void putAt(final int offset, final byte[] source, final int sourceOffset, final int length) {
        putAt(offset, wrap(source), sourceOffset, length);
    }

    void putAt(int offset, Buffer source, int sourceOffset, int length);

    void putFloatAt(final int offset, final float value);

    void putDoubleAt(final int offset, final double value);

    void putByteAt(final int offset, final byte value);

    void putShortAt(final int offset, final short value);

    default void putShortBeAt(final int offset, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        putShortAt(offset, value);
    }

    default void putShortLeAt(final int offset, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        putShortAt(offset, value);
    }

    void putIntAt(final int offset, final int value);

    default void putIntBeAt(final int offset, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        putIntAt(offset, value);
    }

    default void putIntLeAt(final int offset, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        putIntAt(offset, value);
    }

    void putLongAt(final int offset, final long value);

    default void putLongBeAt(final int offset, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        putLongAt(offset, value);
    }

    default void putLongLeAt(final int offset, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        putLongAt(offset, value);
    }

    @Override
    void close();
}
