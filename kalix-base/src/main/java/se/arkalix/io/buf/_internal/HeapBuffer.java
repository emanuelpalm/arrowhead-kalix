package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util._internal.BinaryMath;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Internal
public class HeapBuffer extends CheckedBuffer {
    private final int maximumCapacity;

    private byte[] byteArray;
    private int writeEnd;

    public HeapBuffer(final byte[] byteArray, final int maximumCapacity) {
        if (byteArray == null) {
            throw new NullPointerException("byteArray");
        }
        if (byteArray.length > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        this.byteArray = byteArray;
        this.maximumCapacity = maximumCapacity;

        writeEnd = byteArray.length;
    }

    private HeapBuffer(final byte[] byteArray, final int writeEnd, final int maximumCapacity) {
        this.byteArray = byteArray;
        this.writeEnd = writeEnd;
        this.maximumCapacity = maximumCapacity;
    }

    @Override
    protected Buffer copyUnchecked(final int offset, final int length) {
        final var byteArrayCopy = Arrays.copyOfRange(byteArray, offset, offset + length);
        final var copy = new HeapBuffer(byteArrayCopy, length, maximumCapacity);
        copy.offsets(0, length);
        return copy;
    }

    @Override
    protected Buffer dupeUnchecked() {
        final var dupe = new HeapBuffer(byteArray, writeEnd, byteArray.length);
        dupe.offsets(readOffset(), writeOffset());
        return dupe;
    }

    @Override
    public int writeEnd() {
        return writeEnd;
    }

    @Override
    public void writeEnd(final int writeEnd) {
        checkIfOpen();
        if (writeEnd < 0 || writeEnd > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        if (byteArray.length >= writeEnd) {
            truncateOffsetsTo(writeEnd);
            this.writeEnd = writeEnd;
            return;
        }
        final var newByteArray = new byte[writeEnd];
        System.arraycopy(byteArray, 0, newByteArray, 0, writeOffset());
        byteArray = newByteArray;
    }

    @Override
    public int writeEndMax() {
        return maximumCapacity;
    }

    @Override
    public void onClose() {
        byteArray = null;
    }

    @Override
    protected void getAtUnchecked(
        final int offset,
        final byte[] destination,
        final int destinationOffset,
        final int length
    ) {
        System.arraycopy(byteArray, offset, destination, destinationOffset, length);
    }

    @Override
    protected void getAtUnchecked(
        final int offset,
        final BufferWriter destination,
        final int destinationOffset,
        final int length
    ) {
        destination.setAt(destinationOffset, byteArray, offset, length);
    }

    @Override
    protected void getAtUnchecked(final int offset, final ByteBuffer destination) {
        destination.put(byteArray, offset, writeEnd - offset);
    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        return byteArray[offset];
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        return (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            ? getS16LeAtUnchecked(offset)
            : getS16BeAtUnchecked(offset);
    }

    @Override
    public short getS16BeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 2);
        return getS16BeAtUnchecked(offset);
    }

    protected short getS16BeAtUnchecked(final int offset) {
        return BinaryMath.getS16BeAt(byteArray, offset);
    }

    @Override
    public short getS16LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 2);
        return getS16LeAtUnchecked(offset);
    }

    protected short getS16LeAtUnchecked(final int offset) {
        return BinaryMath.getS16LeAt(byteArray, offset);
    }

    @Override
    protected int getS32AtUnchecked(final int offset) {
        return (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            ? getS32LeAtUnchecked(offset)
            : getS32BeAtUnchecked(offset);
    }

    @Override
    public int getS32BeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 4);
        return getS32BeAtUnchecked(offset);
    }

    protected int getS32BeAtUnchecked(final int offset) {
        return BinaryMath.getS32BeAt(byteArray, offset);
    }

    @Override
    public int getS32LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 4);
        return getS32LeAtUnchecked(offset);
    }

    protected int getS32LeAtUnchecked(final int offset) {
        return BinaryMath.getS32LeAt(byteArray, offset);
    }

    @Override
    protected long getS64AtUnchecked(final int offset) {
        return (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            ? getS64LeAtUnchecked(offset)
            : getS64BeAtUnchecked(offset);
    }

    @Override
    public long getS64BeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 8);
        return getS64BeAtUnchecked(offset);
    }

    protected long getS64BeAtUnchecked(final int offset) {
        return BinaryMath.getS64BeAt(byteArray, offset);
    }

    @Override
    public long getS64LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 8);
        return getS64LeAtUnchecked(offset);
    }

    protected long getS64LeAtUnchecked(final int offset) {
        return BinaryMath.getS64LeAt(byteArray, offset);
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, final int sourceOffset, final int length) {
        System.arraycopy(source, sourceOffset, byteArray, offset, length);
    }

    @Override
    protected void setAtUnchecked(
        final int offset,
        final BufferReader source,
        final int sourceOffset,
        final int length
    ) {
        source.getAt(sourceOffset, byteArray, offset, length);
    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {
        source.get(byteArray, offset, writeEnd - offset);
    }

    @Override
    protected void fillAtUnchecked(final int offset, final byte value, final int length) {
        Arrays.fill(byteArray, offset, offset + length, value);
    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {
        byteArray[offset] = value;
    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {
        BinaryMath.setS16NeAt(byteArray, offset, value);
    }

    @Override
    public void setS16BeAt(final int offset, final short value) {
        checkIfOpen();
        ensureWriteRange(offset, 2);
        BinaryMath.setS16BeAt(byteArray, offset, value);
    }

    @Override
    public void setS16LeAt(final int offset, final short value) {
        checkIfOpen();
        ensureWriteRange(offset, 2);
        BinaryMath.setS16LeAt(byteArray, offset, value);
    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {
        BinaryMath.setS32NeAt(byteArray, offset, value);
    }

    @Override
    public void setS32BeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 4);
        BinaryMath.setS32BeAt(byteArray, offset, value);
    }

    @Override
    public void setS32LeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 4);
        BinaryMath.setS32LeAt(byteArray, offset, value);
    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {
        BinaryMath.setS64NeAt(byteArray, offset, value);
    }

    @Override
    public void setS64BeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 8);
        BinaryMath.setS64BeAt(byteArray, offset, value);
    }

    @Override
    public void setS64LeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 8);
        BinaryMath.setS64LeAt(byteArray, offset, value);
    }
}
