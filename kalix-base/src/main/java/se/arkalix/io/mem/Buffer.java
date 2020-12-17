package se.arkalix.io.mem;

import se.arkalix.io.mem._internal.NioBuffer;

import java.nio.ByteOrder;

public interface Buffer {
    static Buffer wrap(final byte[] byteArray) {
        return NioBuffer.wrap(byteArray, 0, byteArray.length);
    }

    static Buffer wrap(final byte[] byteArray, final int offset, final int length) {
        return NioBuffer.wrap(byteArray, offset, length);
    }

    int length();

    default void getAt(final int position, final byte[] destination) {
        getAt(position, destination, 0, destination.length);
    }

    default void getAt(final int position, final Buffer destination) {
        getAt(position, destination, 0, destination.length());
    }

    default void getAt(final int position, final byte[] destination, int destinationPosition, int length) {
        getAt(position, wrap(destination), destinationPosition, length);
    }

    default void getAt(final int position, final Buffer destination, final int destinationPosition, final int length) {
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        destination.putAt(destinationPosition, this, position, length);
    }

    float getFloatAt(final int position);

    double getDoubleAt(final int position);

    byte getByteAt(final int position);

    short getShortAt(final int position);

    default short getShortBeAt(final int position) {
        var value = getShortAt(position);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default short getShortLeAt(final int position) {
        var value = getShortAt(position);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    int getIntAt(final int position);

    default int getIntBeAt(final int position) {
        var value = getIntAt(position);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default int getIntLeAt(final int position) {
        var value = getIntAt(position);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    long getLongAt(final int position);

    default long getI64BeAt(final int position) {
        var value = getLongAt(position);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default long getLongLeAt(final int position) {
        var value = getLongAt(position);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default void putAt(final int position, final byte[] source) {
        putAt(position, source, 0, source.length);
    }

    default void putAt(final int position, final Buffer source) {
        putAt(position, source, 0, source.length());
    }

    default void putAt(final int position, final byte[] source, final int sourcePosition, final int length) {
        putAt(position, wrap(source), sourcePosition, length);
    }

    void putAt(int position, Buffer source, int sourcePosition, int length);

    void putByteAt(final int position, final byte value);

    void putShortAt(final int position, final short value);

    default void putShortBeAt(final int position, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        putShortAt(position, value);
    }

    default void putShortLeAt(final int position, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        putShortAt(position, value);
    }

    void putIntAt(final int position, final int value);

    default void putIntBeAt(final int position, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        putIntAt(position, value);
    }

    default void putIntLeAt(final int position, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        putIntAt(position, value);
    }

    void putLongAt(final int position, final long value);

    default void putLongBeAt(final int position, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        putLongAt(position, value);
    }

    default void putLongLeAt(final int position, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        putLongAt(position, value);
    }
}
