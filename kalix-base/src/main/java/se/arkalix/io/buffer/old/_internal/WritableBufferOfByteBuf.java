package se.arkalix.io.buffer.old._internal;

import se.arkalix.io.buffer.old.WritableBuffer;
import se.arkalix.util.annotation.Internal;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

@Internal
public class WritableBufferOfByteBuf implements WritableBuffer {
    private final ByteBuf byteBuf;

    public WritableBufferOfByteBuf(final ByteBuf byteBuf) {
        this.byteBuf = Objects.requireNonNull(byteBuf, "byteBuf");
    }

    @Override
    public int writeOffset() {
        return byteBuf.writerIndex();
    }

    @Override
    public void writeOffset(final int offset) {
        byteBuf.writerIndex(offset);
    }

    @Override
    public int writableBytes() {
        return byteBuf.writableBytes();
    }

    @Override
    public void write(final byte b) {
        byteBuf.writeByte(b);
    }

    @Override
    public void write(final byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }
}
