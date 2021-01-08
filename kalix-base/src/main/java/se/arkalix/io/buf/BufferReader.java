package se.arkalix.io.buf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public interface BufferReader extends AutoCloseable {
    int readableBytes();

    int readableBytesFrom(int readOffset);

    int readOffset();

    void readOffset(int readOffset);

    int readEnd();

    default void getAt(final int offset, final byte[] destination) {
        getAt(offset, destination, 0, destination.length);
    }

    void getAt(int offset, byte[] destination, int destinationOffset, int length);

    default void getAt(final int offset, final BufferWriter destination) {
        getAt(offset, destination, destination.writableBytes());
    }

    default void getAt(int offset, BufferWriter destination, int length) {
        getAt(offset, destination, destination.writeOffset(), length);
        destination.writeOffset(destination.writeOffset() + length);
    }

    void getAt(int offset, BufferWriter destination, int destinationOffset, int length);

    void getAt(int offset, ByteBuffer destination);

    default float getF32At(final int offset) {
        return Float.intBitsToFloat(getS32NeAt(offset));
    }

    default double getF64At(final int offset) {
        return Double.longBitsToDouble(getS64NeAt(offset));
    }

    byte getS8At(int offset);

    default short getS16BeAt(final int offset) {
        var value = getS16NeAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    short getS16NeAt(int offset);

    default short getS16LeAt(final int offset) {
        var value = getS16NeAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default int getS24BeAt(final int offset) {
        var value = getU24BeAt(offset);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    default int getS24NeAt(final int offset) {
        var value = getU24NeAt(offset);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    default int getS24LeAt(final int offset) {
        var value = getU24LeAt(offset);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    default int getS32BeAt(final int offset) {
        var value = getS32NeAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    int getS32NeAt(final int offset);

    default int getS32LeAt(final int offset) {
        var value = getS32NeAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default long getS48BeAt(final int offset) {
        var value = getU48BeAt(offset);
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    default long getS48NeAt(final int offset) {
        var value = getU48NeAt(offset);
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    default long getS48LeAt(final int offset) {
        var value = getU48LeAt(offset);
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    default long getS64BeAt(final int offset) {
        var value = getS64NeAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    long getS64NeAt(final int offset);

    default long getS64LeAt(final int offset) {
        var value = getS64NeAt(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default int getU8At(final int offset) {
        return Byte.toUnsignedInt(getS8At(offset));
    }

    default int getU16BeAt(final int offset) {
        return Short.toUnsignedInt(getS16BeAt(offset));
    }

    default int getU16NeAt(final int offset) {
        return Short.toUnsignedInt(getS16NeAt(offset));
    }

    default int getU16LeAt(final int offset) {
        return Short.toUnsignedInt(getS16LeAt(offset));
    }

    int getU24BeAt(final int offset);

    int getU24NeAt(final int offset);

    int getU24LeAt(final int offset);

    default long getU32BeAt(final int offset) {
        return Integer.toUnsignedLong(getS32BeAt(offset));
    }

    default long getU32NeAt(final int offset) {
        return Integer.toUnsignedLong(getS32NeAt(offset));
    }

    default long getU32LeAt(final int offset) {
        return Integer.toUnsignedLong(getS32LeAt(offset));
    }

    long getU48BeAt(final int offset);

    long getU48NeAt(final int offset);

    long getU48LeAt(final int offset);

    default void read(final byte[] destination) {
        read(destination, 0, destination.length);
    }

    void read(byte[] destination, int destinationOffset, int length);

    default void read(final BufferWriter destination) {
        read(destination, destination.writableBytes());
    }

    default void read(final BufferWriter destination, final int length) {
        read(destination, destination.writeOffset(), length);
        destination.writeOffset(destination.writeOffset() + length);
    }

    void read(BufferWriter destination, int destinationOffset, int length);

    void read(ByteBuffer destination);

    default float readF32() {
        return Float.intBitsToFloat(readS32Ne());
    }

    default double readF64() {
        return Double.longBitsToDouble(readS64Ne());
    }

    byte readS8();

    default short readS16Be() {
        var value = readS16Ne();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    short readS16Ne();

    default short readS16Le() {
        var value = readS16Ne();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default int readS24Be() {
        var value = readU24Be();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    default int readS24Ne() {
        var value = readU24Ne();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    default int readS24Le() {
        var value = readU24Le();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    default int readS32Be() {
        var value = readS32Ne();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    int readS32Ne();

    default int readS32Le() {
        var value = readS32Ne();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default long readS48Be() {
        var value = readU48Be();
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    default long readS48Ne() {
        var value = readU48Ne();
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    default long readS48Le() {
        var value = readU48Le();
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    default long readS64Be() {
        var value = readS64Ne();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    long readS64Ne();

    default long readS64Le() {
        var value = readS64Ne();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default int readU8() {
        return Byte.toUnsignedInt(readS8());
    }

    default int readU16Be() {
        return Short.toUnsignedInt(readS16Be());
    }

    default int readU16Ne() {
        return Short.toUnsignedInt(readS16Ne());
    }

    default int readU16Le() {
        return Short.toUnsignedInt(readS16Le());
    }

    int readU24Be();

    int readU24Ne();

    int readU24Le();

    default long readU32Be() {
        return Integer.toUnsignedLong(readS32Be());
    }

    default long readU32Ne() {
        return Integer.toUnsignedLong(readS32Ne());
    }

    default long readU32Le() {
        return Integer.toUnsignedLong(readS32Le());
    }

    long readU48Be();

    long readU48Ne();

    long readU48Le();

    void skip(int bytesToSkip);

    boolean isClosed();

    @Override
    void close();
}
