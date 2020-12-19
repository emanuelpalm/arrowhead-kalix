package se.arkalix.io.buf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface BufferWriter {
    int writableBytes();

    void writableBytes(int writableBytes);

    default int writableBytesMax() {
        return writeEndMax() - writeOffset();
    }

    int writeEnd();

    void writeEnd(int writeEnd);

    int writeEndMax();

    int writeOffset();

    void writeOffset(int readOffset);

    default void setAt(final int offset, final byte[] source) {
        setAt(offset, source, 0, source.length);
    }

    void setAt(int offset, byte[] source, int sourceOffset, int length);

    default void setAt(final int offset, final BufferWriter source) {
        setAt(offset, source, source.writableBytes());
    }

    void setAt(int offset, BufferWriter source, int length);

    void setAt(int offset, BufferWriter source, int sourceOffset, int length);

    void setAt(int offset, ByteBuffer source);

    void fillAt(int offset, byte value, int length);

    default void zeroAt(final int offset, final int length) {
        fillAt(offset, (byte) 0, length);
    }

    default void setF32At(final int offset, final float value) {
        setS32At(offset, Float.floatToIntBits(value));
    }

    default void setF64At(final int offset, final double value) {
        setS64At(offset, Double.doubleToLongBits(value));
    }

    void setS8At(int offset, byte value);

    void setS16At(int offset, short value);

    default void setS16BeAt(final int offset, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        setS16At(offset, value);
    }

    default void setS16LeAt(final int offset, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        setS16At(offset, value);
    }

    void setS24At(final int offset, final int value);

    void setS24BeAt(final int offset, final int value);

    void setS24LeAt(final int offset, final int value);

    void setS32At(final int offset, final int value);

    default void setS32BeAt(final int offset, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        setS32At(offset, value);
    }

    default void setS32LeAt(final int offset, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        setS32At(offset, value);
    }

    void setS48At(final int offset, final long value);

    void setS48BeAt(final int offset, final long value);

    void setS48LeAt(final int offset, final long value);

    void setS64At(final int offset, final long value);

    default void setS64BeAt(final int offset, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        setS64At(offset, value);
    }

    default void setS64LeAt(final int offset, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        setS64At(offset, value);
    }

    default void setU8At(final int offset, final int value) {
        setS8At(offset, (byte) value);
    }

    default void setU16At(final int offset, final int value) {
        setS16At(offset, (short) value);
    }

    default void setU16BeAt(final int offset, final int value) {
        setS16BeAt(offset, (short) value);
    }

    default void setU16LeAt(final int offset, final int value) {
        setS16LeAt(offset, (short) value);
    }

    default void setU24At(final int offset, final int value) {
        setS24At(offset, value);
    }

    default void setU24BeAt(final int offset, final int value) {
        setS24BeAt(offset, value);
    }

    default void setU24LeAt(final int offset, final int value) {
        setS24LeAt(offset, value);
    }

    default void setU32At(final int offset, final long value) {
        setS32At(offset, (int) value);
    }

    default void setU32BeAt(final int offset, final long value) {
        setS32BeAt(offset, (int) value);
    }

    default void setU32LeAt(final int offset, final long value) {
        setS32LeAt(offset, (int) value);
    }

    default void setU48At(final int offset, final long value) {
        setS48At(offset, value);
    }

    default void setU48BeAt(final int offset, final long value) {
        setS48BeAt(offset, value);
    }

    default void setU48LeAt(final int offset, final long value) {
        setS48LeAt(offset, value);
    }
}
