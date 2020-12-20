package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util._internal.BinaryMath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BufferBase implements Buffer {
    private int readOffset = 0;
    private int writeOffset = 0;
    private boolean isClosed = false;

    @Override
    public Buffer copy(final int offset, final int length) {
        checkIfOpen();
        throw new UnsupportedOperationException("not implemented"); // TODO: Implement.
    }

    @Override
    public Buffer dupe() {
        checkIfOpen();
        throw new UnsupportedOperationException("not implemented"); // TODO: Implement.
    }

    @Override
    public Buffer slice(final int offset, final int length) {
        checkIfOpen();
        throw new UnsupportedOperationException("not implemented"); // TODO: Implement.
    }

    @Override
    public void offsets(final int readOffset, final int writeOffset) {
        checkOffsets(readOffset, writeOffset, writeEnd());
        this.readOffset = readOffset;
        this.writeOffset = writeOffset;
    }

    @Override
    public void clear() {
        readOffset = 0;
        writeOffset = 0;
    }

    @Override
    public BufferReader reader() {
        checkIfOpen();
        throw new UnsupportedOperationException("not implemented"); // TODO: Implement.
    }

    @Override
    public BufferWriter writer() {
        checkIfOpen();
        throw new UnsupportedOperationException("not implemented"); // TODO: Implement.
    }

    @Override
    public int readableBytes() {
        return writeOffset - readOffset;
    }

    @Override
    public int readOffset() {
        return readOffset;
    }

    @Override
    public void readOffset(final int readOffset) {
        checkOffsets(readOffset, writeOffset, writeEnd());
        this.readOffset = readOffset;
    }

    @Override
    public int readEnd() {
        return writeOffset;
    }

    @Override
    public void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        checkIfOpen();
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        checkWriteRange(destinationOffset, length, destination.length);
        checkReadRange(offset, length);
        getAtUnchecked(offset, destination, destinationOffset, length);
    }

    protected abstract void getAtUnchecked(int offset, byte[] destination, int destinationOffset, int length);

    @Override
    public void getAt(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {
        checkIfOpen();
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        checkReadRange(offset, length);
        getAtUnchecked(offset, destination, destinationOffset, length);
    }

    protected abstract void getAtUnchecked(int offset, BufferWriter destination, int destinationOffset, int length);

    @Override
    public void getAt(final int offset, final ByteBuffer destination) {
        checkIfOpen();
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        checkReadRange(offset, destination.remaining());
        getAtUnchecked(offset, destination);
    }

    protected abstract void getAtUnchecked(int offset, ByteBuffer destination);

    @Override
    public byte getS8At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 1);
        return getS8AtUnchecked(offset);
    }

    protected abstract byte getS8AtUnchecked(int offset);

    @Override
    public short getS16At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 2);
        return getS16AtUnchecked(offset);
    }

    protected abstract short getS16AtUnchecked(int offset);

    @Override
    public int getS32At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 4);
        return getS32AtUnchecked(offset);
    }

    protected abstract short getS32AtUnchecked(int offset);

    @Override
    public long getS64At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 8);
        return getS64AtUnchecked(offset);
    }

    protected abstract short getS64AtUnchecked(int offset);

    private int getU8AtUnchecked(final int offset) {
        return Byte.toUnsignedInt(getS8AtUnchecked(offset));
    }

    @Override
    public int getU24At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 3);
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
            ? getU24LeAtUnchecked(offset)
            : getU24BeAtUnchecked(offset);
    }

    private int getU24AtUnchecked(final int offset) {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
            ? getU24LeAtUnchecked(offset)
            : getU24BeAtUnchecked(offset);
    }

    @Override
    public int getU24BeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 3);
        return getU24BeAtUnchecked(offset);
    }

    private int getU24BeAtUnchecked(final int offset) {
        return (getU8AtUnchecked(offset) & 0xff) << 16 |
            (getU8AtUnchecked(offset + 1) & 0xff) << 8 |
            getU8AtUnchecked(offset + 2) & 0xff;
    }

    @Override
    public int getU24LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 3);
        return getU24LeAtUnchecked(offset);
    }

    private int getU24LeAtUnchecked(final int offset) {
        return getU8AtUnchecked(offset) & 0xff |
            (getU8AtUnchecked(offset + 1) & 0xff) << 8 |
            (getU8AtUnchecked(offset + 2) & 0xff) << 16;
    }

    @Override
    public long getU48At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 6);
        return getU48AtUnchecked(offset);
    }

    private long getU48AtUnchecked(final int offset) {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
            ? getU48LeAtUnchecked(offset)
            : getU48BeAtUnchecked(offset);
    }

    @Override
    public long getU48BeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 6);
        return getU48BeAtUnchecked(offset);
    }

    private long getU48BeAtUnchecked(final int offset) {
        return (long) (getU8AtUnchecked(offset) & 0xff) << 40 |
            (long) (getU8AtUnchecked(offset + 1) & 0xff) << 32 |
            (long) (getU8AtUnchecked(offset + 2) & 0xff) << 24 |
            (long) (getU8AtUnchecked(offset + 3) & 0xff) << 16 |
            (long) (getU8AtUnchecked(offset + 4) & 0xff) << 8 |
            (long) (getU8AtUnchecked(offset + 5) & 0xff);
    }

    @Override
    public long getU48LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 6);
        return getU48LeAtUnchecked(offset);
    }

    private long getU48LeAtUnchecked(final int offset) {
        return (long) (getU8AtUnchecked(offset) & 0xff) |
            (long) (getU8AtUnchecked(offset + 1) & 0xff) << 8 |
            (long) (getU8AtUnchecked(offset + 2) & 0xff) << 16 |
            (long) (getU8AtUnchecked(offset + 3) & 0xff) << 24 |
            (long) (getU8AtUnchecked(offset + 4) & 0xff) << 32 |
            (long) (getU8AtUnchecked(offset + 5) & 0xff) << 40;
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        checkIfOpen();
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        checkWriteRange(destinationOffset, length, destination.length);
        checkReadLength(length);
        getAtUnchecked(readOffset, destination, destinationOffset, length);
        readOffset += length;
    }

    @Override
    public void read(final BufferWriter destination, final int destinationOffset, final int length) {
        checkIfOpen();
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        checkReadLength(length);
        getAtUnchecked(readOffset, destination, destinationOffset, length);
        readOffset += length;
    }

    @Override
    public void read(final ByteBuffer destination) {
        checkIfOpen();
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        final var remaining = destination.remaining();
        checkReadLength(remaining);
        getAtUnchecked(readOffset, destination);
        readOffset += remaining;
    }

    @Override
    public byte readS8() {
        checkIfOpen();
        checkReadLength(1);
        final var value = getS8AtUnchecked(readOffset);
        readOffset += 1;
        return value;
    }

    @Override
    public short readS16() {
        checkIfOpen();
        checkReadLength(2);
        final var value = getS16AtUnchecked(readOffset);
        readOffset += 2;
        return value;
    }

    @Override
    public int readS32() {
        checkIfOpen();
        checkReadLength(4);
        final var value = getS32AtUnchecked(readOffset);
        readOffset += 4;
        return value;
    }

    @Override
    public long readS64() {
        checkIfOpen();
        checkReadLength(8);
        final var value = getS64AtUnchecked(readOffset);
        readOffset += 8;
        return value;
    }

    @Override
    public int readU24() {
        checkIfOpen();
        checkReadLength(3);
        final var value = getU24AtUnchecked(readOffset);
        readOffset += 3;
        return value;
    }

    @Override
    public int readU24Be() {
        checkIfOpen();
        checkReadLength(3);
        final var value = getU24BeAtUnchecked(readOffset);
        readOffset += 3;
        return value;
    }

    @Override
    public int readU24Le() {
        checkIfOpen();
        checkReadLength(3);
        final var value = getU24LeAtUnchecked(readOffset);
        readOffset += 3;
        return value;
    }

    @Override
    public long readU48() {
        checkIfOpen();
        checkReadLength(6);
        final var value = getU48AtUnchecked(readOffset);
        readOffset += 6;
        return value;
    }

    @Override
    public long readU48Be() {
        checkIfOpen();
        checkReadLength(6);
        final var value = getU48BeAtUnchecked(readOffset);
        readOffset += 6;
        return value;
    }

    @Override
    public long readU48Le() {
        checkIfOpen();
        checkReadLength(6);
        final var value = getU48LeAtUnchecked(readOffset);
        readOffset += 6;
        return value;
    }

    @Override
    public void skip(final int bytesToSkip) {
        checkIfOpen();
        checkReadLength(bytesToSkip);
        readOffset += bytesToSkip;
    }

    @Override
    public int writableBytes() {
        return writeEnd() - writeOffset;
    }

    @Override
    public void writableBytesFrom(final int writeOffset, final int writableBytes) {
        ensureWriteRange(writeOffset, writableBytes);
    }

    @Override
    public int writeOffset() {
        return writeOffset;
    }

    @Override
    public void writeOffset(final int writeOffset) {
        checkOffsets(readOffset, writeOffset, writeEnd());
        this.writeOffset = writeOffset;
    }

    @Override
    public void setAt(final int offset, final byte[] source, final int sourceOffset, final int length) {
        checkIfOpen();
        if (source == null) {
            throw new NullPointerException("source");
        }
        checkReadRange(sourceOffset, length, source.length);
        ensureWriteRange(offset, length);
        setAtUnchecked(offset, source, sourceOffset, length);
    }

    protected abstract void setAtUnchecked(int offset, byte[] source, int sourceOffset, int length);

    @Override
    public void setAt(final int offset, final BufferReader source, final int sourceOffset, final int length) {
        checkIfOpen();
        if (source == null) {
            throw new NullPointerException("source");
        }
        ensureWriteRange(offset, length);
        setAtUnchecked(offset, source, sourceOffset, length);
    }

    protected abstract void setAtUnchecked(int offset, BufferReader source, int sourceOffset, int length);

    @Override
    public void setAt(final int offset, final ByteBuffer source) {
        checkIfOpen();
        if (source == null) {
            throw new NullPointerException("source");
        }
        ensureWriteRange(offset, source.remaining());
        setAtUnchecked(offset, source);
    }

    protected abstract void setAtUnchecked(int offset, ByteBuffer source);

    @Override
    public void setAt(final int offset, final byte value, final int length) {
        checkIfOpen();
        ensureWriteRange(offset, length);
        setAtUnchecked(offset, value, length);
    }

    protected abstract void setAtUnchecked(int offset, byte value, int length);

    @Override
    public void setS8At(final int offset, final byte value) {
        checkIfOpen();
        ensureWriteRange(offset, 1);
        setS8AtUnchecked(offset, value);
    }

    protected abstract void setS8AtUnchecked(final int offset, final byte value);

    @Override
    public void setS16At(final int offset, final short value) {
        checkIfOpen();
        ensureWriteRange(offset, 2);
        setS16AtUnchecked(offset, value);
    }

    protected abstract void setS16AtUnchecked(final int offset, final short value);

    @Override
    public void setS24At(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 3);
        setS24AtUnchecked(offset, value);
    }

    private void setS24AtUnchecked(final int offset, final int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            setS24LeAtUnchecked(offset, value);
        }
        else {
            setS24BeAtUnchecked(offset, value);
        }
    }

    @Override
    public void setS24BeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 3);
        setS24BeAtUnchecked(offset, value);
    }

    private void setS24BeAtUnchecked(final int offset, final int value) {
        setS8AtUnchecked(offset, (byte) (value >>> 16));
        setS8AtUnchecked(offset + 1, (byte) (value >>> 8));
        setS8AtUnchecked(offset + 2, (byte) value);
    }

    @Override
    public void setS24LeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 3);
        setS24LeAtUnchecked(offset, value);
    }

    private void setS24LeAtUnchecked(final int offset, final int value) {
        setS8AtUnchecked(offset, (byte) value);
        setS8AtUnchecked(offset + 1, (byte) (value >>> 8));
        setS8AtUnchecked(offset + 2, (byte) (value >>> 16));
    }

    @Override
    public void setS32At(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 4);
        setS32AtUnchecked(offset, value);
    }

    protected abstract void setS32AtUnchecked(int offset, int value);

    @Override
    public void setS48At(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 6);
        setS48AtUnchecked(offset, value);
    }

    protected void setS48AtUnchecked(final int offset, final long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            setS48LeAtUnchecked(offset, value);
        }
        else {
            setS48BeAtUnchecked(offset, value);
        }
    }

    @Override
    public void setS48BeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 6);
        setS48BeAtUnchecked(offset, value);
    }

    protected void setS48BeAtUnchecked(final int offset, final long value) {
        setS8AtUnchecked(offset, (byte) (value >>> 40));
        setS8AtUnchecked(offset + 1, (byte) (value >>> 32));
        setS8AtUnchecked(offset + 2, (byte) (value >>> 24));
        setS8AtUnchecked(offset + 3, (byte) (value >>> 16));
        setS8AtUnchecked(offset + 4, (byte) (value >>> 8));
        setS8AtUnchecked(offset + 5, (byte) value);
    }

    @Override
    public void setS48LeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 6);
        setS48LeAtUnchecked(offset, value);
    }

    protected void setS48LeAtUnchecked(final int offset, final long value) {
        setS8AtUnchecked(offset, (byte) value);
        setS8AtUnchecked(offset + 1, (byte) (value >>> 8));
        setS8AtUnchecked(offset + 2, (byte) (value >>> 16));
        setS8AtUnchecked(offset + 3, (byte) (value >>> 24));
        setS8AtUnchecked(offset + 4, (byte) (value >>> 32));
        setS8AtUnchecked(offset + 5, (byte) (value >>> 40));
    }

    @Override
    public void setS64At(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 8);
        setS64AtUnchecked(offset, value);
    }

    protected abstract void setS64AtUnchecked(int offset, long value);

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        checkIfOpen();
        if (source == null) {
            throw new NullPointerException("source");
        }
        checkReadRange(sourceOffset, length, source.length);
        ensureWriteLength(length);
        setAtUnchecked(writeOffset, source, sourceOffset, length);
        writeOffset += length;
    }

    @Override
    public void write(final BufferReader source, final int sourceOffset, final int length) {
        checkIfOpen();
        if (source == null) {
            throw new NullPointerException("source");
        }
        ensureWriteLength(length);
        setAtUnchecked(readOffset, source, sourceOffset, length);
        writeOffset += length;
    }

    @Override
    public void write(final ByteBuffer source) {
        checkIfOpen();
        if (source == null) {
            throw new NullPointerException("source");
        }
        final var remaining = source.remaining();
        ensureWriteLength(remaining);
        getAtUnchecked(writeOffset, source);
        writeOffset += remaining;
    }

    @Override
    public void write(final byte value, final int length) {
        checkIfOpen();
        ensureWriteLength(length);
        setAtUnchecked(writeOffset, value, length);
    }

    @Override
    public void writeS8(final byte value) {
        checkIfOpen();
        ensureWriteLength(1);
        setS8AtUnchecked(writeOffset, value);
        writeOffset += 1;
    }

    @Override
    public void writeS16(final short value) {
        checkIfOpen();
        ensureWriteLength(2);
        setS16AtUnchecked(writeOffset, value);
        writeOffset += 2;
    }

    @Override
    public void writeS24(final int value) {
        checkIfOpen();
        ensureWriteLength(3);
        setS24AtUnchecked(writeOffset, value);
        writeOffset += 3;
    }

    @Override
    public void writeS24Be(final int value) {
        checkIfOpen();
        ensureWriteLength(3);
        setS24BeAtUnchecked(writeOffset, value);
        writeOffset += 3;
    }

    @Override
    public void writeS24Le(final int value) {
        checkIfOpen();
        ensureWriteLength(3);
        setS24LeAtUnchecked(writeOffset, value);
        writeOffset += 3;
    }

    @Override
    public void writeS32(final int value) {
        checkIfOpen();
        ensureWriteLength(4);
        setS32AtUnchecked(writeOffset, value);
        writeOffset += 4;
    }

    @Override
    public void writeS48(final long value) {
        checkIfOpen();
        ensureWriteLength(6);
        setS48AtUnchecked(writeOffset, value);
        writeOffset += 6;
    }

    @Override
    public void writeS48Be(final long value) {
        checkIfOpen();
        ensureWriteLength(6);
        setS48BeAtUnchecked(writeOffset, value);
        writeOffset += 6;
    }

    @Override
    public void writeS48Le(final long value) {
        checkIfOpen();
        ensureWriteLength(6);
        setS48LeAtUnchecked(writeOffset, value);
        writeOffset += 6;
    }

    @Override
    public void writeS64(final long value) {
        checkIfOpen();
        ensureWriteLength(8);
        setS64AtUnchecked(writeOffset, value);
        writeOffset += 8;
    }

    @Override
    public final void close() {
        isClosed = true;
        onClose();
    }

    protected abstract void onClose();

    private void checkIfOpen() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
    }

    private static void checkOffsets(final int readOffset, final int writeOffset, final int writeEnd) {
        if (readOffset < 0 || readOffset > writeOffset || writeOffset > writeEnd) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void checkReadRange(final int readOffset, final int length) {
        if (BinaryMath.isRangeOutOfBounds(readOffset, length, writeOffset)) {
            throw new IndexOutOfBoundsException();
        }
    }

    private static void checkReadRange(final int readOffset, final int length, final int readEnd) {
        if (BinaryMath.isRangeOutOfBounds(readOffset, length, readEnd)) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void checkReadLength(final int bytesToRead) {
        if (readOffset > writeOffset - bytesToRead) {
            throw new IndexOutOfBoundsException();
        }
    }

    private static void checkWriteRange(final int writeOffset, final int length, final int writeEnd) {
        if (BinaryMath.isRangeOutOfBounds(writeOffset, length, writeEnd)) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void ensureWriteRange(final int writeOffset, final int length) {
        final var rangeEnd = writeOffset + length;
        if (rangeEnd < writeEnd()) {
            return;
        }
        if (rangeEnd > writeEndMax()) {
            throw new IndexOutOfBoundsException();
        }
        writeEnd(rangeEnd);
    }

    private void ensureWriteLength(final int byteToWrite) {
        ensureWriteRange(writeOffset, byteToWrite);
    }
}
