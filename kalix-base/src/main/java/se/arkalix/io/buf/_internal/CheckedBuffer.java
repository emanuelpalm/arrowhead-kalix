package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util._internal.BinaryMath;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Internal
public abstract class CheckedBuffer implements Buffer {
    private int readOffset;
    private int writeOffset;
    private boolean isClosed = false;

    @Override
    public Buffer copy(final int offset, final int length) {
        checkIfOpen();
        checkCopyRange(offset, length);
        return copyUnchecked(offset, length);
    }

    protected abstract Buffer copyUnchecked(final int offset, final int length);

    @Override
    public final Buffer dupe() {
        checkIfOpen();
        return dupeUnchecked();
    }

    protected abstract Buffer dupeUnchecked();

    @Override
    public void offsets(final int readOffset, final int writeOffset) {
        checkOffsets(readOffset, writeOffset, writeEnd());
        this.readOffset = readOffset;
        this.writeOffset = writeOffset;
    }

    protected void truncateOffsetsTo(final int writeEnd) {
        if (writeEnd < writeOffset) {
            readOffset = Math.min(readOffset, writeEnd);
            writeOffset = writeEnd;
        }
    }

    @Override
    public void clear() {
        readOffset = 0;
        writeOffset = 0;
    }

    @Override
    public BufferReader reader() {
        checkIfOpen();
        return Buffer.super.reader();
    }

    @Override
    public BufferWriter writer() {
        checkIfOpen();
        return Buffer.super.writer();
    }

    @Override
    public int readableBytes() {
        return writeOffset - readOffset;
    }

    @Override
    public int readableBytesFrom(final int readOffset) {
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

    protected abstract int getS32AtUnchecked(int offset);

    @Override
    public long getS64At(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 8);
        return getS64AtUnchecked(offset);
    }

    protected abstract long getS64AtUnchecked(int offset);

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
        final var byteArray = new byte[3];
        getAtUnchecked(offset, byteArray, 0, 3);
        return BinaryMath.getS24BeAt(byteArray, 0);
    }

    @Override
    public int getU24LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 3);
        return getU24LeAtUnchecked(offset);
    }

    private int getU24LeAtUnchecked(final int offset) {
        final var byteArray = new byte[3];
        getAtUnchecked(offset, byteArray, 0, 3);
        return BinaryMath.getS24LeAt(byteArray, 0);
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
        final var byteArray = new byte[6];
        getAtUnchecked(offset, byteArray, 0, 6);
        return BinaryMath.getS48BeAt(byteArray, 0);
    }

    @Override
    public long getU48LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 6);
        return getU48LeAtUnchecked(offset);
    }

    private long getU48LeAtUnchecked(final int offset) {
        final var byteArray = new byte[6];
        getAtUnchecked(offset, byteArray, 0, 6);
        return BinaryMath.getS48LeAt(byteArray, 0);
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
    public void fillAt(final int offset, final byte value, final int length) {
        checkIfOpen();
        ensureWriteRange(offset, length);
        fillAtUnchecked(offset, value, length);
    }

    protected abstract void fillAtUnchecked(int offset, byte value, int length);

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
        final var byteArray = new byte[3];
        BinaryMath.setS24BeAt(byteArray, 0, value);
        setAtUnchecked(offset, byteArray, 0, 3);
    }

    @Override
    public void setS24LeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 3);
        setS24LeAtUnchecked(offset, value);
    }

    private void setS24LeAtUnchecked(final int offset, final int value) {
        final var byteArray = new byte[3];
        BinaryMath.setS24LeAt(byteArray, 0, value);
        setAtUnchecked(offset, byteArray, 0, 3);
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
        final var byteArray = new byte[6];
        BinaryMath.setS48BeAt(byteArray, 0, value);
        setAtUnchecked(offset, byteArray, 0, 6);
    }

    @Override
    public void setS48LeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 6);
        setS48LeAtUnchecked(offset, value);
    }

    protected void setS48LeAtUnchecked(final int offset, final long value) {
        final var byteArray = new byte[6];
        BinaryMath.setS48LeAt(byteArray, 0, value);
        setAtUnchecked(offset, byteArray, 0, 6);
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
        setAtUnchecked(writeOffset, source);
        writeOffset += remaining;
    }

    @Override
    public void fill(final byte value, final int length) {
        checkIfOpen();
        ensureWriteLength(length);
        fillAtUnchecked(writeOffset, value, length);
        writeOffset += length;
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

    protected void checkIfOpen() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
    }

    protected static void checkOffsets(final int readOffset, final int writeOffset, final int writeEnd) {
        if (readOffset < 0 || readOffset > writeOffset || writeOffset > writeEnd) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected void checkCopyRange(final int copyOffset, final int length) {
        if (BinaryMath.isRangeOutOfBounds(copyOffset, length, writeEnd())) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected void checkReadRange(final int readOffset, final int length) {
        if (BinaryMath.isRangeOutOfBounds(readOffset, length, writeOffset)) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected static void checkReadRange(final int readOffset, final int length, final int readEnd) {
        if (BinaryMath.isRangeOutOfBounds(readOffset, length, readEnd)) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected void checkReadLength(final int bytesToRead) {
        if (readOffset > writeOffset - bytesToRead) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected static void checkWriteRange(final int writeOffset, final int length, final int writeEnd) {
        if (BinaryMath.isRangeOutOfBounds(writeOffset, length, writeEnd)) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected void ensureWriteRange(final int writeOffset, final int length) {
        final var rangeEnd = writeOffset + length;
        if (rangeEnd <= writeEnd()) {
            return;
        }
        if (rangeEnd > writeEndMax()) {
            throw new IndexOutOfBoundsException();
        }
        writeEnd(rangeEnd);
    }

    protected void ensureWriteLength(final int byteToWrite) {
        ensureWriteRange(writeOffset, byteToWrite);
    }
}
