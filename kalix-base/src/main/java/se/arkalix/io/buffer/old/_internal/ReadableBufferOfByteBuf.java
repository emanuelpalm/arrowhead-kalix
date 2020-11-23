package se.arkalix.io.buffer.old._internal;

import se.arkalix.io.buffer.old.ReadableBuffer;
import se.arkalix.util.annotation.Internal;
import io.netty.buffer.ByteBuf;

@Internal
public class ReadableBufferOfByteBuf implements ReadableBuffer {
    private final ByteBuf byteBuf;

    public ReadableBufferOfByteBuf(final ByteBuf buf) {
        byteBuf = buf;
    }

    @Override
    public int readOffset() {
        return byteBuf.readerIndex();
    }

    @Override
    public void readOffset(final int offset) {
        byteBuf.readerIndex(offset);
    }

    @Override
    public int readableBytes() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte getByte(final int offset) {
        return byteBuf.getByte(offset);
    }

    @Override
    public byte peekByte() {
        return byteBuf.getByte(byteBuf.readerIndex());
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public void readBytes(final byte[] target) {
        byteBuf.readBytes(target);
    }

    @Override
    public void readBytes(final byte[] target, final int targetOffset, final int length) {
        byteBuf.readBytes(target, targetOffset, length);
    }

    @Override
    public void getBytes(final int offset, final byte[] target) {
        byteBuf.getBytes(offset, target);
    }

    public void getBytes(final int offset, final byte[] target, final int targetOffset, final int length) {
        byteBuf.getBytes(offset, target, targetOffset, length);
    }

    @Override
    public void skipByte() {
        byteBuf.readByte();
    }

    @Override
    public void skipBytes(final int n) {
        byteBuf.skipBytes(n);
    }
}
