package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Internal
public class NioBuffer extends AbstractBuffer {
    private final int maximumCapacity;

    private ByteBuffer byteBuffer;

    public NioBuffer(final ByteBuffer byteBuffer, final int maximumCapacity) {
        if (byteBuffer == null) {
            throw new NullPointerException("byteBuffer");
        }
        if (byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("byteBuffer.isReadOnly() == true");
        }
        if (byteBuffer.capacity() > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        this.byteBuffer = byteBuffer;
        this.maximumCapacity = maximumCapacity;

        byteBuffer.clear();
        byteBuffer.order(ByteOrder.nativeOrder());
    }

    @Override
    protected Buffer copyUnchecked(final int readOffset, final int length) {
        final var writeOffset = readOffset + length;
        final var byteBufferCopy = ByteBuffer.allocateDirect(byteBuffer.capacity() - readOffset)
            .put(byteBuffer.asReadOnlyBuffer()
                .position(readOffset)
                .limit(writeOffset))
            .position(0)
            .limit(length);

        final var copy = new NioBuffer(byteBufferCopy, maximumCapacity);
        copy.offsets(readOffset, writeOffset);
        return copy;
    }

    @Override
    protected Buffer dupeUnchecked(final int readOffset, final int length) {
        final var writeOffset = readOffset + length;
        final var byteBufferDupe = byteBuffer.duplicate()
            .position(readOffset)
            .limit(writeOffset);

        final var dupe = new NioBuffer(byteBufferDupe, byteBuffer.capacity());
        dupe.offsets(readOffset, writeOffset);
        return dupe;
    }

    @Override
    public int writeEnd() {
        return byteBuffer.limit();
    }

    @Override
    public void writeEnd(final int writeEnd) {
        checkIfOpen();
        if (writeEnd < 0 || writeEnd > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        if (byteBuffer.capacity() >= writeEnd) {
            byteBuffer.limit(writeEnd);
            truncateOffsetsTo(writeEnd);
            return;
        }
        byteBuffer = onExpand(byteBuffer, writeEnd);
    }

    protected ByteBuffer onExpand(final ByteBuffer oldByteBuffer, final int newCapacity) {
        return ByteBuffer.allocateDirect(newCapacity)
            .position(0)
            .limit(writeOffset())
            .put(oldByteBuffer.position(0)
                .limit(writeOffset()))
            .clear();
    }

    @Override
    public int writeEndMax() {
        return maximumCapacity;
    }

    @Override
    protected void getAtUnchecked(
        final int offset,
        final byte[] destination,
        final int destinationOffset,
        final int length
    ) {
        byteBuffer.asReadOnlyBuffer()
            .clear()
            .position(offset)
            .limit(offset + length)
            .get(destination, destinationOffset, length);
    }

    @Override
    protected void getAtUnchecked(
        final int offset,
        final BufferWriter destination,
        final int destinationOffset,
        final int length
    ) {
        destination.setAt(destinationOffset, byteBuffer.asReadOnlyBuffer()
            .clear()
            .position(offset)
            .limit(offset + length));
    }

    @Override
    protected void getAtUnchecked(final int offset, final ByteBuffer destination) {
        destination.put(byteBuffer.asReadOnlyBuffer()
            .clear()
            .position(offset)
            .limit(offset + destination.remaining()));
    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        return byteBuffer.get(offset);
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        return byteBuffer.getShort(offset);
    }

    @Override
    protected int getS32AtUnchecked(final int offset) {
        return byteBuffer.getInt(offset);
    }

    @Override
    protected long getS64AtUnchecked(final int offset) {
        return byteBuffer.getLong(offset);
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, final int sourceOffset, final int length) {
        byteBuffer.duplicate()
            .clear()
            .position(offset)
            .limit(offset + length)
            .put(source, sourceOffset, length);
    }

    @Override
    protected void setAtUnchecked(
        final int offset,
        final BufferReader source,
        final int sourceOffset,
        final int length
    ) {
        source.getAt(sourceOffset, byteBuffer.duplicate()
            .clear()
            .position(offset)
            .limit(offset + length));
    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {
        byteBuffer.duplicate()
            .clear()
            .position(offset)
            .limit(offset + source.remaining())
            .put(source);
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte value, int length) {
        if (byteBuffer.hasArray()) {
            final var offset0 = byteBuffer.arrayOffset() + offset;
            Arrays.fill(byteBuffer.array(), offset0, offset0 + length, value);
            return;
        }

        final var duplicate = byteBuffer.duplicate()
            .clear()
            .position(offset)
            .limit(offset + length);

        while (length-- > 0) {
            duplicate.put(value);
        }
    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {
        byteBuffer.put(offset, value);
    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {
        byteBuffer.putShort(offset, value);
    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {
        byteBuffer.putInt(offset, value);
    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {
        byteBuffer.putLong(offset, value);
    }

    @Override
    protected void onClose() {
        byteBuffer = null;
    }
}
