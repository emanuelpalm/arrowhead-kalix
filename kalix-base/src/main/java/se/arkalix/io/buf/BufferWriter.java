package se.arkalix.io.buf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface BufferWriter extends AutoCloseable {
    int writableBytes();

    default void writableBytes(int writableBytes) {
        writableBytesFrom(writeOffset(), writableBytes);
    }

    void writableBytesFrom(int writeOffset, int writableBytes);

    default int writableBytesMax() {
        return writeEndMax() - writeOffset();
    }

    int writeEnd();

    void writeEnd(int writeEnd);

    int writeEndMax();

    int writeOffset();

    void writeOffset(int writeOffset);

    default void setAt(final int offset, final byte[] source) {
        setAt(offset, source, 0, source.length);
    }

    void setAt(int offset, byte[] source, int sourceOffset, int length);

    default void setAt(final int offset, final BufferReader source) {
        setAt(offset, source, source.readableBytes());
    }

    default void setAt(int offset, BufferReader source, int length) {
        setAt(offset, source, source.readOffset(), length);
        source.readOffset(source.readOffset() + length);
    }

    void setAt(int offset, BufferReader source, int sourceOffset, int length);

    void setAt(int offset, ByteBuffer source);

    void setAt(int offset, byte value, int length);

    default void setZeroAt(final int offset, final int length) {
        setAt(offset, (byte) 0, length);
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

    default void write(final byte[] source) {
        write(source, 0, source.length);
    }

    void write(byte[] source, int sourceOffset, int length);

    default void write(final BufferReader source) {
        write(source, source.readableBytes());
    }

    default void write(BufferReader source, int length) {
        write(source, source.readOffset(), length);
        source.readOffset(source.readOffset() + length);
    }

    void write(BufferReader source, int sourceOffset, int length);

    void write(ByteBuffer source);

    void write(byte value, int length);

    default void writeZero(final int length) {
        write((byte) 0, length);
    }

    default void writeF32(final float value) {
        writeS32(Float.floatToIntBits(value));
    }

    default void writeF64(final double value) {
        writeS64(Double.doubleToLongBits(value));
    }

    void writeS8(byte value);

    void writeS16(short value);

    default void writeS16Be(short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        writeS16(value);
    }

    default void writeS16Le(short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        writeS16(value);
    }

    void writeS24(final int value);

    void writeS24Be(final int value);

    void writeS24Le(final int value);

    void writeS32(final int value);

    default void writeS32Be(int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        writeS32(value);
    }

    default void writeS32Le(int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        writeS32(value);
    }

    void writeS48(final long value);

    void writeS48Be(final long value);

    void writeS48Le(final long value);

    void writeS64(final long value);

    default void writeS64Be(long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        writeS64(value);
    }

    default void writeS64Le(long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        writeS64(value);
    }

    default void writeU8(final int value) {
        writeS8((byte) value);
    }

    default void writeU16(final int value) {
        writeS16((short) value);
    }

    default void writeU16Be(final int value) {
        writeS16Be((short) value);
    }

    default void writeU16Le(final int value) {
        writeS16Le((short) value);
    }

    default void writeU24(final int value) {
        writeS24(value);
    }

    default void writeU24Be(final int value) {
        writeS24Be(value);
    }

    default void writeU24Le(final int value) {
        writeS24Le(value);
    }

    default void writeU32(final long value) {
        writeS32((int) value);
    }

    default void writeU32Be(final long value) {
        writeS32Be((int) value);
    }

    default void writeU32Le(final long value) {
        writeS32Le((int) value);
    }

    default void writeU48(final long value) {
        writeS48(value);
    }

    default void writeU48Be(final long value) {
        writeS48Be(value);
    }

    default void writeU48Le(final long value) {
        writeS48Le(value);
    }

    @Override
    void close();
}