package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;

import java.nio.ByteBuffer;

public class BufferSlice implements Buffer {
    private final int innerBaseOffset;
    private final int writeEndMax;

    private Buffer inner;
    private int readOffset = 0;
    private int writeOffset = 0;
    private int writeEnd;

    public static BufferSlice of(final Buffer buffer, final int offset, final int length) {
        return new BufferSlice(buffer, offset, length);
    }

    private BufferSlice(final Buffer buffer, final int offset, final int length) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        if (offset < 0 || offset > length || length > buffer.writeEnd()) {
            throw new IndexOutOfBoundsException();
        }
        inner = buffer;
        innerBaseOffset = offset;
        writeEndMax = length;
        writeEnd = length;
    }

    @Override
    public Buffer copy(final int offset, final int length) {
        if (offset < 0 || offset > length || offset + length > writeEnd) {
            throw new IndexOutOfBoundsException();
        }
        return of(inner.copy(intoInnerOffset(offset), length), offset, length);
    }

    private int fromInnerOffset(final int offset) {
        return offset - this.innerBaseOffset;
    }

    private int intoInnerOffset(final int offset) {
        return offset + this.innerBaseOffset;
    }

    @Override
    public Buffer dupe() {
        return of(inner.dupe(), innerBaseOffset, writeEnd);
    }

    @Override
    public Buffer slice(final int offset, final int length) {
        if (offset < 0 || offset > length || offset + length > writeEnd) {
            throw new IndexOutOfBoundsException();
        }
        return inner.slice(intoInnerOffset(offset), length);
    }

    @Override
    public void offsets(final int readOffset, final int writeOffset) {
        inner.offsets(intoInnerOffset(readOffset), intoInnerOffset(writeOffset));
    }

    @Override
    public void clear() {
        inner.clear();
    }

    @Override
    public int readableBytes() {
        return inner.readableBytes();
    }

    @Override
    public int readableBytesFrom(final int readOffset) {
        return inner.readableBytesFrom(intoInnerOffset(readOffset));
    }

    @Override
    public int readOffset() {
        return fromInnerOffset(inner.readOffset());
    }

    @Override
    public void readOffset(final int readOffset) {
        inner.readOffset(intoInnerOffset(readOffset));
    }

    @Override
    public int readEnd() {
        return fromInnerOffset(inner.readEnd());
    }

    @Override
    public void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {

    }

    @Override
    public void getAt(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {

    }

    @Override
    public void getAt(final int offset, final ByteBuffer destination) {

    }

    @Override
    public byte getS8At(final int offset) {
        return 0;
    }

    @Override
    public short getS16At(final int offset) {
        return 0;
    }

    @Override
    public int getS32At(final int offset) {
        return 0;
    }

    @Override
    public long getS64At(final int offset) {
        return 0;
    }

    @Override
    public int getU24At(final int offset) {
        return 0;
    }

    @Override
    public int getU24BeAt(final int offset) {
        return 0;
    }

    @Override
    public int getU24LeAt(final int offset) {
        return 0;
    }

    @Override
    public long getU48At(final int offset) {
        return 0;
    }

    @Override
    public long getU48BeAt(final int offset) {
        return 0;
    }

    @Override
    public long getU48LeAt(final int offset) {
        return 0;
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {

    }

    @Override
    public void read(final BufferWriter destination, final int destinationOffset, final int length) {

    }

    @Override
    public void read(final ByteBuffer destination) {

    }

    @Override
    public byte readS8() {
        return 0;
    }

    @Override
    public short readS16() {
        return 0;
    }

    @Override
    public int readS32() {
        return 0;
    }

    @Override
    public long readS64() {
        return 0;
    }

    @Override
    public int readU24() {
        return 0;
    }

    @Override
    public int readU24Be() {
        return 0;
    }

    @Override
    public int readU24Le() {
        return 0;
    }

    @Override
    public long readU48() {
        return 0;
    }

    @Override
    public long readU48Be() {
        return 0;
    }

    @Override
    public long readU48Le() {
        return 0;
    }

    @Override
    public void skip(final int bytesToSkip) {

    }

    @Override
    public int writableBytes() {
        return 0;
    }

    @Override
    public void writableBytesFrom(final int writeOffset, final int writableBytes) {

    }

    @Override
    public int writeEnd() {
        return 0;
    }

    @Override
    public void writeEnd(final int writeEnd) {

    }

    @Override
    public int writeEndMax() {
        return 0;
    }

    @Override
    public int writeOffset() {
        return 0;
    }

    @Override
    public void writeOffset(final int writeOffset) {

    }

    @Override
    public void setAt(final int offset, final byte[] source, final int sourceOffset, final int length) {

    }

    @Override
    public void setAt(final int offset, final BufferReader source, final int sourceOffset, final int length) {

    }

    @Override
    public void setAt(final int offset, final ByteBuffer source) {

    }

    @Override
    public void setAt(final int offset, final byte value, final int length) {

    }

    @Override
    public void setS8At(final int offset, final byte value) {

    }

    @Override
    public void setS16At(final int offset, final short value) {

    }

    @Override
    public void setS24At(final int offset, final int value) {

    }

    @Override
    public void setS24BeAt(final int offset, final int value) {

    }

    @Override
    public void setS24LeAt(final int offset, final int value) {

    }

    @Override
    public void setS32At(final int offset, final int value) {

    }

    @Override
    public void setS48At(final int offset, final long value) {

    }

    @Override
    public void setS48BeAt(final int offset, final long value) {

    }

    @Override
    public void setS48LeAt(final int offset, final long value) {

    }

    @Override
    public void setS64At(final int offset, final long value) {

    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {

    }

    @Override
    public void write(final BufferReader source, final int sourceOffset, final int length) {

    }

    @Override
    public void write(final ByteBuffer source) {

    }

    @Override
    public void write(final byte value, final int length) {

    }

    @Override
    public void writeS8(final byte value) {

    }

    @Override
    public void writeS16(final short value) {

    }

    @Override
    public void writeS24(final int value) {

    }

    @Override
    public void writeS24Be(final int value) {

    }

    @Override
    public void writeS24Le(final int value) {

    }

    @Override
    public void writeS32(final int value) {

    }

    @Override
    public void writeS48(final long value) {

    }

    @Override
    public void writeS48Be(final long value) {

    }

    @Override
    public void writeS48Le(final long value) {

    }

    @Override
    public void writeS64(final long value) {

    }

    @Override
    public void close() {

    }
}
