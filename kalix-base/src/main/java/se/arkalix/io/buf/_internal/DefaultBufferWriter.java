package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.Objects;

@Internal
public class DefaultBufferWriter implements BufferWriter {
    private BufferWriter inner;

    public DefaultBufferWriter(final BufferWriter inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public int writableBytes() {
        return inner.writableBytes();
    }

    @Override
    public void writableBytesFrom(final int writeOffset, final int writableBytes) {
        inner.writableBytesFrom(writeOffset, writableBytes);
    }

    @Override
    public int writeEnd() {
        return inner.writeEnd();
    }

    @Override
    public void writeEnd(final int writeEnd) {
        inner.writeEnd(writeEnd);
    }

    @Override
    public int writeEndMax() {
        return inner.writeEndMax();
    }

    @Override
    public int writeOffset() {
        return inner.writeOffset();
    }

    @Override
    public void writeOffset(final int writeOffset) {
        inner.writeOffset(writeOffset);
    }

    @Override
    public void setAt(final int offset, final byte[] source, final int sourceOffset, final int length) {
        inner.setAt(offset, source, sourceOffset, length);
    }

    @Override
    public void setAt(final int offset, final BufferReader source, final int sourceOffset, final int length) {
        inner.setAt(offset, source, sourceOffset, length);
    }

    @Override
    public void setAt(final int offset, final ByteBuffer source) {
        inner.setAt(offset, source);
    }

    @Override
    public void fillAt(final int offset, final byte value, final int length) {
        inner.fillAt(offset, value, length);
    }

    @Override
    public void setS8At(final int offset, final byte value) {
        inner.setS8At(offset, value);
    }

    @Override
    public void setS16At(final int offset, final short value) {
        inner.setS16At(offset, value);
    }

    @Override
    public void setS24At(final int offset, final int value) {
        inner.setS24At(offset, value);
    }

    @Override
    public void setS24BeAt(final int offset, final int value) {
        inner.setS24BeAt(offset, value);
    }

    @Override
    public void setS24LeAt(final int offset, final int value) {
        inner.setS24LeAt(offset, value);
    }

    @Override
    public void setS32At(final int offset, final int value) {
        inner.setS32At(offset, value);
    }

    @Override
    public void setS48At(final int offset, final long value) {
        inner.setS48At(offset, value);
    }

    @Override
    public void setS48BeAt(final int offset, final long value) {
        inner.setS48BeAt(offset, value);
    }

    @Override
    public void setS48LeAt(final int offset, final long value) {
        inner.setS48LeAt(offset, value);
    }

    @Override
    public void setS64At(final int offset, final long value) {
        inner.setS64At(offset, value);
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        inner.write(source, sourceOffset, length);
    }

    @Override
    public void write(final BufferReader source, final int sourceOffset, final int length) {
        inner.write(source, sourceOffset, length);
    }

    @Override
    public void write(final ByteBuffer source) {
        inner.write(source);
    }

    @Override
    public void fill(final byte value, final int length) {
        inner.fill(value, length);
    }

    @Override
    public void writeS8(final byte value) {
        inner.writeS8(value);
    }

    @Override
    public void writeS16(final short value) {
        inner.writeS16(value);
    }

    @Override
    public void writeS24(final int value) {
        inner.writeS24(value);
    }

    @Override
    public void writeS24Be(final int value) {
        inner.writeS24Be(value);
    }

    @Override
    public void writeS24Le(final int value) {
        inner.writeS24Le(value);
    }

    @Override
    public void writeS32(final int value) {
        inner.writeS32(value);
    }

    @Override
    public void writeS48(final long value) {
        inner.writeS48(value);
    }

    @Override
    public void writeS48Be(final long value) {
        inner.writeS48Be(value);
    }

    @Override
    public void writeS48Le(final long value) {
        inner.writeS48Le(value);
    }

    @Override
    public void writeS64(final long value) {
        inner.writeS64(value);
    }

    @Override
    public void close() {
        inner = ClosedBuffer.instance();
    }
}
