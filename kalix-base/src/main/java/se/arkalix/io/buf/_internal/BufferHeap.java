package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Internal
public class BufferHeap extends BufferBase {
    private final int maximumCapacity;

    private byte[] byteArray;
    private int writeEnd;

    public BufferHeap(final byte[] byteArray, final int maximumCapacity) {
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

    private BufferHeap(final byte[] byteArray, final int writeEnd, final int maximumCapacity) {
        this.byteArray = byteArray;
        this.writeEnd = writeEnd;
        this.maximumCapacity = maximumCapacity;
    }

    @Override
    protected Buffer copyUnchecked(final int readOffset, final int length) {
        final var writeOffset = readOffset + length;
        final var copy = new BufferHeap(
            Arrays.copyOfRange(byteArray, readOffset, writeOffset),
            writeOffset,
            maximumCapacity
        );
        copy.offsets(readOffset, writeOffset);
        return copy;
    }

    @Override
    protected Buffer dupeUnchecked(final int readOffset, final int length) {
        final var writeOffset = readOffset + length;
        final var dupe = new BufferHeap(byteArray, writeOffset, byteArray.length);
        dupe.offsets(readOffset, writeOffset);
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
        byteArray = onExpand(byteArray, writeEnd);
    }

    protected byte[] onExpand(final byte[] oldByteArray, final int newCapacity) {
        final var newByteArray = new byte[newCapacity];
        System.arraycopy(oldByteArray, 0, newByteArray, 0, writeOffset());
        return newByteArray;
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
        return (short) (byteArray[offset] << 8 | byteArray[offset + 1] & 0xFF);
    }

    @Override
    public short getS16LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 2);
        return getS16LeAtUnchecked(offset);
    }

    protected short getS16LeAtUnchecked(final int offset) {
        return (short) (byteArray[offset] & 0xff | byteArray[offset + 1] << 8);
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
        return (byteArray[offset] & 0xff) << 24 |
            (byteArray[offset + 1] & 0xff) << 16 |
            (byteArray[offset + 2] & 0xff) << 8 |
            byteArray[offset + 3] & 0xff;
    }

    @Override
    public int getS32LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 4);
        return getS32LeAtUnchecked(offset);
    }

    protected int getS32LeAtUnchecked(final int offset) {
        return byteArray[offset] & 0xff |
            (byteArray[offset + 1] & 0xff) << 8 |
            (byteArray[offset + 2] & 0xff) << 16 |
            (byteArray[offset + 3] & 0xff) << 24;
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
        return ((long) byteArray[offset] & 0xff) << 56 |
            ((long) byteArray[offset + 1] & 0xff) << 48 |
            ((long) byteArray[offset + 2] & 0xff) << 40 |
            ((long) byteArray[offset + 3] & 0xff) << 32 |
            ((long) byteArray[offset + 4] & 0xff) << 24 |
            ((long) byteArray[offset + 5] & 0xff) << 16 |
            ((long) byteArray[offset + 6] & 0xff) << 8 |
            (long) byteArray[offset + 7] & 0xff;
    }

    @Override
    public long getS64LeAt(final int offset) {
        checkIfOpen();
        checkReadRange(offset, 8);
        return getS64LeAtUnchecked(offset);
    }

    protected long getS64LeAtUnchecked(final int offset) {
        return (long) byteArray[offset] & 0xff |
            ((long) byteArray[offset + 1] & 0xff) << 8 |
            ((long) byteArray[offset + 2] & 0xff) << 16 |
            ((long) byteArray[offset + 3] & 0xff) << 24 |
            ((long) byteArray[offset + 4] & 0xff) << 32 |
            ((long) byteArray[offset + 5] & 0xff) << 40 |
            ((long) byteArray[offset + 6] & 0xff) << 48 |
            ((long) byteArray[offset + 7] & 0xff) << 56;
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
    protected void setAtUnchecked(final int offset, final byte value, final int length) {
        Arrays.fill(byteArray, offset, offset + length, value);
    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {
        byteArray[offset] = value;
    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            setS16LeAtUnchecked(offset, value);
        }
        else {
            setS16BeAtUnchecked(offset, value);
        }
    }

    @Override
    public void setS16BeAt(final int offset, final short value) {
        checkIfOpen();
        ensureWriteRange(offset, 2);
        setS16BeAtUnchecked(offset, value);
    }

    protected void setS16BeAtUnchecked(final int offset, final short value) {
        byteArray[offset] = (byte) (value >>> 8);
        byteArray[offset + 1] = (byte) value;
    }

    @Override
    public void setS16LeAt(final int offset, final short value) {
        checkIfOpen();
        ensureWriteRange(offset, 2);
        setS16LeAtUnchecked(offset, value);
    }

    protected void setS16LeAtUnchecked(final int offset, final short value) {
        byteArray[offset] = (byte) value;
        byteArray[offset + 1] = (byte) (value >>> 8);
    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            setS32LeAtUnchecked(offset, value);
        }
        else {
            setS32BeAtUnchecked(offset, value);
        }
    }

    @Override
    public void setS32BeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 4);
        setS32BeAtUnchecked(offset, value);
    }

    protected void setS32BeAtUnchecked(final int offset, final int value) {
        byteArray[offset] = (byte) (value >>> 24);
        byteArray[offset + 1] = (byte) (value >>> 16);
        byteArray[offset + 2] = (byte) (value >>> 8);
        byteArray[offset + 3] = (byte) value;
    }

    @Override
    public void setS32LeAt(final int offset, final int value) {
        checkIfOpen();
        ensureWriteRange(offset, 4);
        setS32LeAtUnchecked(offset, value);
    }

    protected void setS32LeAtUnchecked(final int offset, final int value) {
        byteArray[offset] = (byte) value;
        byteArray[offset + 1] = (byte) (value >>> 8);
        byteArray[offset + 2] = (byte) (value >>> 16);
        byteArray[offset + 3] = (byte) (value >>> 24);
    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            setS64LeAtUnchecked(offset, value);
        }
        else {
            setS64BeAtUnchecked(offset, value);
        }
    }

    @Override
    public void setS64BeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 8);
        setS64BeAtUnchecked(offset, value);
    }

    protected void setS64BeAtUnchecked(final int offset, final long value) {
        byteArray[offset] = (byte) (value >>> 56);
        byteArray[offset + 1] = (byte) (value >>> 48);
        byteArray[offset + 2] = (byte) (value >>> 40);
        byteArray[offset + 3] = (byte) (value >>> 32);
        byteArray[offset + 4] = (byte) (value >>> 24);
        byteArray[offset + 5] = (byte) (value >>> 16);
        byteArray[offset + 6] = (byte) (value >>> 8);
        byteArray[offset + 7] = (byte) value;
    }

    @Override
    public void setS64LeAt(final int offset, final long value) {
        checkIfOpen();
        ensureWriteRange(offset, 8);
        setS64LeAtUnchecked(offset, value);
    }

    protected void setS64LeAtUnchecked(final int offset, final long value) {
        byteArray[offset] = (byte) value;
        byteArray[offset + 1] = (byte) (value >>> 8);
        byteArray[offset + 2] = (byte) (value >>> 16);
        byteArray[offset + 3] = (byte) (value >>> 24);
        byteArray[offset + 4] = (byte) (value >>> 32);
        byteArray[offset + 5] = (byte) (value >>> 40);
        byteArray[offset + 6] = (byte) (value >>> 48);
        byteArray[offset + 7] = (byte) (value >>> 56);
    }
}
