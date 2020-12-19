package se.arkalix.io.buf0;

import java.nio.ByteOrder;

public interface BufferMutator extends AutoCloseable {
    int length();

    default void setAt(final int offset, final byte[] source) {
        setAt(offset, source, 0, source.length);
    }

    default void setAt(final int offset, final BufferAccessor source) {
        setAt(offset, source, 0, source.length());
    }

    void setAt(int offset, byte[] source, int sourceOffset, int length);

    void setAt(int offset, BufferAccessor source, int sourceOffset, int length);

    void setFloatAt(final int offset, final float value);

    void setDoubleAt(final int offset, final double value);

    void setByteAt(final int offset, final byte value);

    void SetShortAt(final int offset, final short value);

    default void setShortBeAt(final int offset, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        SetShortAt(offset, value);
    }

    default void setShortLeAt(final int offset, short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        SetShortAt(offset, value);
    }

    void setIntAt(final int offset, final int value);

    default void setIntBeAt(final int offset, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        setIntAt(offset, value);
    }

    default void setIntLeAt(final int offset, int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        setIntAt(offset, value);
    }

    void setLongAt(final int offset, final long value);

    default void setLongBeAt(final int offset, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        setLongAt(offset, value);
    }

    default void setLongLeAt(final int offset, long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        setLongAt(offset, value);
    }

    @Override
    void close();
}
