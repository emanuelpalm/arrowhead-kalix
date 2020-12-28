package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.Objects;

@Internal
public class DefaultBufferReader implements BufferReader {
    private BufferReader inner;

    public DefaultBufferReader(final BufferReader inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public int readableBytes() {
        return inner.readableBytes();
    }

    @Override
    public int readableBytesFrom(final int readOffset) {
        return inner.readableBytesFrom(readOffset);
    }

    @Override
    public int readOffset() {
        return inner.readOffset();
    }

    @Override
    public void readOffset(final int readOffset) {
        inner.readOffset(readOffset);
    }

    @Override
    public int readEnd() {
        return inner.readEnd();
    }

    @Override
    public void getAt(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        inner.getAt(offset, destination, destinationOffset, length);
    }

    @Override
    public void getAt(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {
        inner.getAt(offset, destination, destinationOffset, length);
    }

    @Override
    public void getAt(final int offset, final ByteBuffer destination) {
        inner.getAt(offset, destination);
    }

    @Override
    public byte getS8At(final int offset) {
        return inner.getS8At(offset);
    }

    @Override
    public short getS16At(final int offset) {
        return inner.getS16At(offset);
    }

    @Override
    public int getS32At(final int offset) {
        return inner.getS32At(offset);
    }

    @Override
    public long getS64At(final int offset) {
        return inner.getS64At(offset);
    }

    @Override
    public int getU24At(final int offset) {
        return inner.getU24At(offset);
    }

    @Override
    public int getU24BeAt(final int offset) {
        return inner.getU24BeAt(offset);
    }

    @Override
    public int getU24LeAt(final int offset) {
        return inner.getU24LeAt(offset);
    }

    @Override
    public long getU48At(final int offset) {
        return inner.getU48At(offset);
    }

    @Override
    public long getU48BeAt(final int offset) {
        return inner.getU48BeAt(offset);
    }

    @Override
    public long getU48LeAt(final int offset) {
        return inner.getU48LeAt(offset);
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        inner.read(destination, destinationOffset, length);
    }

    @Override
    public void read(final BufferWriter destination, final int destinationOffset, final int length) {
        inner.read(destination, destinationOffset, length);
    }

    @Override
    public void read(final ByteBuffer destination) {
        inner.read(destination);
    }

    @Override
    public byte readS8() {
        return 0;
    }

    @Override
    public short readS16() {
        return inner.readS16();
    }

    @Override
    public int readS32() {
        return inner.readS32();
    }

    @Override
    public long readS64() {
        return inner.readS64();
    }

    @Override
    public int readU24() {
        return inner.readU24();
    }

    @Override
    public int readU24Be() {
        return inner.readU24Be();
    }

    @Override
    public int readU24Le() {
        return inner.readU24Le();
    }

    @Override
    public long readU48() {
        return inner.readU48();
    }

    @Override
    public long readU48Be() {
        return inner.readU48Be();
    }

    @Override
    public long readU48Le() {
        return inner.readU48Le();
    }

    @Override
    public void skip(final int bytesToSkip) {
        inner.skip(bytesToSkip);
    }

    @Override
    public void close() {
        inner = ClosedBuffer.instance();
    }
}
