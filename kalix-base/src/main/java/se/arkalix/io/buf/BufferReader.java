package se.arkalix.io.buf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface BufferReader {
    int readableBytes();

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

    void getAt(int offset, BufferWriter destination, int length);

    void getAt(int offset, BufferWriter destination, int destinationOffset, int length);

    void getAt(int offset, ByteBuffer destination);

    default float getF32At(final int offset) {
        return Float.intBitsToFloat(getS32At(offset));
    }

    default double getF64At(final int offset) {
        return Double.longBitsToDouble(getS64At(offset));
    }

    byte getS8At(int offset);

    short getS16At(int offset);

    default short getS16BeAt(final int offset) {
        var value = getS16At(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default short getS16LeAt(final int offset) {
        var value = getS16At(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default int getS24At(final int offset) {
        var value = getU24At(offset);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
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

    default int getS24LeAt(final int offset) {
        var value = getU24LeAt(offset);
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    int getS32At(final int offset);

    default int getS32BeAt(final int offset) {
        var value = getS32At(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default int getS32LeAt(final int offset) {
        var value = getS32At(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default long getS48At(final int offset) {
        var value = getU48At(offset);
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
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

    default long getS48LeAt(final int offset) {
        var value = getU48LeAt(offset);
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    long getS64At(final int offset);

    default long getS64BeAt(final int offset) {
        var value = getS64At(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default long getS64LeAt(final int offset) {
        var value = getS64At(offset);
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default int getU8At(final int offset) {
        return Byte.toUnsignedInt(getS8At(offset));
    }

    default int getU16At(final int offset) {
        return Short.toUnsignedInt(getS16At(offset));
    }

    default int getU16BeAt(final int offset) {
        return Short.toUnsignedInt(getS16BeAt(offset));
    }

    default int getU16LeAt(final int offset) {
        return Short.toUnsignedInt(getS16LeAt(offset));
    }

    int getU24At(final int offset);

    int getU24BeAt(final int offset);

    int getU24LeAt(final int offset);

    default long getU32At(final int offset) {
        return Integer.toUnsignedLong(getS32At(offset));
    }

    default long getU32BeAt(final int offset) {
        return Integer.toUnsignedLong(getS32BeAt(offset));
    }

    default long getU32LeAt(final int offset) {
        return Integer.toUnsignedLong(getS32LeAt(offset));
    }

    long getU48At(final int offset);

    long getU48BeAt(final int offset);

    long getU48LeAt(final int offset);

    default void read(final byte[] destination) {
        read(destination, 0, destination.length);
    }

    void read(byte[] destination, int destinationOffset, int length);

    default void read(final BufferWriter destination) {
        read(destination, destination.writableBytes());
    }

    void read(final BufferWriter destination, final int length);

    void read(BufferWriter destination, int destinationOffset, int length);

    void read(ByteBuffer destination);

    default float readF32() {
        return Float.intBitsToFloat(readS32());
    }

    default double readF64() {
        return Double.longBitsToDouble(readS64());
    }

    byte readS8();

    short readS16();

    default short readS16Be() {
        var value = readS16();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default short readS16Le() {
        var value = readS16();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    default int readS24() {
        var value = readU24();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
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

    default int readS24Le() {
        var value = readU24Le();
        if ((value & 0x800000) != 0) {
            value |= 0xff000000;
        }
        return value;
    }

    int readS32();

    default int readS32Be() {
        var value = readS32();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default int readS32Le() {
        var value = readS32();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Integer.reverseBytes(value);
        }
        return value;
    }

    default long readS48() {
        var value = readU48();
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
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

    default long readS48Le() {
        var value = readU48Le();
        if ((value & 0x800000000000L) != 0) {
            value |= 0xffff000000000000L;
        }
        return value;
    }

    long readS64();

    default long readS64Be() {
        var value = readS64();
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default long readS64Le() {
        var value = readS64();
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            value = Long.reverseBytes(value);
        }
        return value;
    }

    default int readU8() {
        return Byte.toUnsignedInt(readS8());
    }

    default int readU16() {
        return Short.toUnsignedInt(readS16());
    }

    default int readU16Be() {
        return Short.toUnsignedInt(readS16Be());
    }

    default int readU16Le() {
        return Short.toUnsignedInt(readS16Le());
    }

    int readU24();

    int readU24Be();

    int readU24Le();

    default long readU32() {
        return Integer.toUnsignedLong(readS32());
    }

    default long readU32Be() {
        return Integer.toUnsignedLong(readS32Be());
    }

    default long readU32Le() {
        return Integer.toUnsignedLong(readS32Le());
    }

    long readU48();

    long readU48Be();

    long readU48Le();

    void skip(int bytesToSkip);
}
