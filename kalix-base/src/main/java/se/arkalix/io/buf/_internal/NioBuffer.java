package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferWriter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NioBuffer implements Buffer {
    private int readOffset = 0;
    private int writeOffset = 0;

    @Override
    public void offsets(final int readOffset, final int writeOffset) {

    }

    @Override
    public void clear() {
        readOffset = 0;
        writeOffset = 0;
    }

    private void throwIfGivenReadOffsetIsOutOfBounds(final int readOffset) {

    }

    private void throwIfGivenReadRangeIsOutOfBounds(final int readOffset, final int length) {

    }

    private void throwIfLessThanGivenNumberOfBytesCanBeRead(final int bytesToRead) {

    }

    private void throwIfWriteOffsetIsOutOfBounds(final int writeOffset) {

    }

    @Override
    public int writableBytes() {
        return 0;
    }

    @Override
    public int writeOffset() {
        return writeOffset;
    }

    @Override
    public void writeOffset(final int writeOffset) {
        throwIfWriteOffsetIsOutOfBounds(writeOffset);
        this.writeOffset = writeOffset;
    }

    @Override
    public int writeEnd() {
        return 0;
    }

    @Override
    public void writeEnd(final int capacity) {

    }

    @Override
    public int writeEndMax() {
        return 0;
    }

    @Override
    public int readableBytes() {
        return 0;
    }

    @Override
    public int readOffset() {
        return readOffset;
    }

    @Override
    public void readOffset(final int readOffset) {
        throwIfGivenReadOffsetIsOutOfBounds(readOffset);
        this.readOffset = readOffset;
    }

    @Override
    public int readEnd() {
        return 0;
    }

    @Override
    public void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        throwIfGivenReadRangeIsOutOfBounds(offset, length);
    }

    @Override
    public void getAt(final int offset, final BufferWriter destination, final int length) {
        getAt(offset, destination, destination.writeOffset(), length);
        destination.writeOffset(destination.writeOffset() + length);
    }

    @Override
    public void getAt(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {
        throwIfGivenReadRangeIsOutOfBounds(offset, length);
    }

    @Override
    public void getAt(final int offset, final ByteBuffer destination) {
        throwIfGivenReadRangeIsOutOfBounds(offset, destination.remaining());
    }

    @Override
    public byte getS8At(final int offset) {
        throwIfGivenReadOffsetIsOutOfBounds(offset);
        return getS8AtUnchecked(offset);
    }

    private byte getS8AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    public short getS16At(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 2);
        return 0;
    }

    @Override
    public int getS32At(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 4);
        return 0;
    }

    @Override
    public long getS64At(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 8);
        return 0;
    }

    private int getU8AtUnchecked(final int offset) {
        return Byte.toUnsignedInt(getS8AtUnchecked(offset));
    }

    @Override
    public int getU24At(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 3);
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
        throwIfGivenReadRangeIsOutOfBounds(offset, 3);
        return getU24BeAtUnchecked(offset);
    }

    private int getU24BeAtUnchecked(final int offset) {
        return (getU8AtUnchecked(offset) & 0xff) << 16 |
            (getU8AtUnchecked(offset + 1) & 0xff) << 8 |
            getU8AtUnchecked(offset + 2) & 0xff;
    }

    @Override
    public int getU24LeAt(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 3);
        return getU24LeAtUnchecked(offset);
    }

    private int getU24LeAtUnchecked(final int offset) {
        return getU8AtUnchecked(offset) & 0xff |
            (getU8AtUnchecked(offset + 1) & 0xff) << 8 |
            (getU8AtUnchecked(offset + 2) & 0xff) << 16;
    }

    @Override
    public long getU48At(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 6);
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
            ? getU48LeAtUnchecked(offset)
            : getU48BeAtUnchecked(offset);
    }

    private long getU48AtUnchecked(final int offset) {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
            ? getU48LeAtUnchecked(offset)
            : getU48BeAtUnchecked(offset);
    }

    @Override
    public long getU48BeAt(final int offset) {
        throwIfGivenReadRangeIsOutOfBounds(offset, 6);
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
        throwIfGivenReadRangeIsOutOfBounds(offset, 6);
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

    }

    @Override
    public void read(final BufferWriter destination, final int length) {
        read(destination, destination.writeOffset(), length);
        destination.writeOffset(destination.writeOffset() + length);
    }

    @Override
    public void read(final BufferWriter destination, final int destinationOffset, final int length) {

    }

    @Override
    public void read(final ByteBuffer destination) {

    }

    @Override
    public byte readS8() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(1);
        return 0;
    }

    @Override
    public short readS16() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(2);
        return 0;
    }

    @Override
    public int readS32() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(4);
        return 0;
    }

    @Override
    public long readS64() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(8);
        return 0;
    }

    @Override
    public int readU24() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(3);
        final var value = getU24AtUnchecked(readOffset);
        readOffset += 3;
        return value;
    }

    @Override
    public int readU24Be() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(3);
        final var value = getU24BeAtUnchecked(readOffset);
        readOffset += 3;
        return value;
    }

    @Override
    public int readU24Le() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(3);
        final var value = getU24LeAtUnchecked(readOffset);
        readOffset += 3;
        return value;
    }

    @Override
    public long readU48() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(6);
        final var value = getU48At(readOffset);
        readOffset += 6;
        return value;
    }

    @Override
    public long readU48Be() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(6);
        final var value = getU48BeAt(readOffset);
        readOffset += 6;
        return value;
    }

    @Override
    public long readU48Le() {
        throwIfLessThanGivenNumberOfBytesCanBeRead(6);
        final var value = getU48LeAt(readOffset);
        readOffset += 6;
        return value;
    }
}
