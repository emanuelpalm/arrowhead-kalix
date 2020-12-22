package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;

@Internal
public class BufferClosed implements Buffer {
    private static final BufferClosed instance = new BufferClosed();

    public static BufferClosed instance() {
        return instance;
    }

    private BufferClosed() {}

    @Override
    public void clear() {
        // Does nothing.
    }

    @Override
    public Buffer copy(final int offset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public Buffer dupe(final int offset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void offsets(final int readOffset, final int writeOffset) {
        throw new BufferIsClosed();
    }

    @Override
    public int readableBytes() {
        throw new BufferIsClosed();
    }

    @Override
    public int readableBytesFrom(final int readOffset) {
        throw new BufferIsClosed();
    }

    @Override
    public int readOffset() {
        throw new BufferIsClosed();
    }

    @Override
    public void readOffset(final int readOffset) {
        throw new BufferIsClosed();
    }

    @Override
    public int readEnd() {
        throw new BufferIsClosed();
    }

    @Override
    public void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void getAt(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void getAt(final int offset, final ByteBuffer destination) {
        throw new BufferIsClosed();
    }

    @Override
    public byte getS8At(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public short getS16At(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public int getS32At(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public long getS64At(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public int getU24At(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public int getU24BeAt(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public int getU24LeAt(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public long getU48At(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public long getU48BeAt(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public long getU48LeAt(final int offset) {
        throw new BufferIsClosed();
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void read(final BufferWriter destination, final int destinationOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void read(final ByteBuffer destination) {
        throw new BufferIsClosed();
    }

    @Override
    public byte readS8() {
        throw new BufferIsClosed();
    }

    @Override
    public short readS16() {
        throw new BufferIsClosed();
    }

    @Override
    public int readS32() {
        throw new BufferIsClosed();
    }

    @Override
    public long readS64() {
        throw new BufferIsClosed();
    }

    @Override
    public int readU24() {
        throw new BufferIsClosed();
    }

    @Override
    public int readU24Be() {
        throw new BufferIsClosed();
    }

    @Override
    public int readU24Le() {
        throw new BufferIsClosed();
    }

    @Override
    public long readU48() {
        throw new BufferIsClosed();
    }

    @Override
    public long readU48Be() {
        throw new BufferIsClosed();
    }

    @Override
    public long readU48Le() {
        throw new BufferIsClosed();
    }

    @Override
    public void skip(final int bytesToSkip) {
        throw new BufferIsClosed();
    }

    @Override
    public int writableBytes() {
        throw new BufferIsClosed();
    }

    @Override
    public void writableBytesFrom(final int writeOffset, final int writableBytes) {
        throw new BufferIsClosed();
    }

    @Override
    public int writeEnd() {
        throw new BufferIsClosed();
    }

    @Override
    public void writeEnd(final int writeEnd) {
        throw new BufferIsClosed();
    }

    @Override
    public int writeEndMax() {
        throw new BufferIsClosed();
    }

    @Override
    public int writeOffset() {
        throw new BufferIsClosed();
    }

    @Override
    public void writeOffset(final int writeOffset) {
        throw new BufferIsClosed();
    }

    @Override
    public void setAt(final int offset, final byte[] source, final int sourceOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void setAt(final int offset, final BufferReader source, final int sourceOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void setAt(final int offset, final ByteBuffer source) {
        throw new BufferIsClosed();
    }

    @Override
    public void setAt(final int offset, final byte value, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS8At(final int offset, final byte value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS16At(final int offset, final short value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS24At(final int offset, final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS24BeAt(final int offset, final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS24LeAt(final int offset, final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS32At(final int offset, final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS48At(final int offset, final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS48BeAt(final int offset, final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS48LeAt(final int offset, final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void setS64At(final int offset, final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void write(final BufferReader source, final int sourceOffset, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void write(final ByteBuffer source) {
        throw new BufferIsClosed();
    }

    @Override
    public void write(final byte value, final int length) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS8(final byte value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS16(final short value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS24(final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS24Be(final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS24Le(final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS32(final int value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS48(final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS48Be(final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS48Le(final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void writeS64(final long value) {
        throw new BufferIsClosed();
    }

    @Override
    public void close() {
        // Does nothing.
    }
}
