package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util._internal.BinaryMath;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class NioPageBuffer extends CheckedBuffer {
    private final int maximumCapacity;

    private int currentCapacity;
    private int writeEnd;

    private NioPagePool pagePool;
    private ArrayList<ByteBuffer> pages;
    private NioPageSet readSet = null;
    private NioPageSet writeSet = null;

    public NioPageBuffer(final int maximumCapacity, final NioPagePool pagePool) {
        if (maximumCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (pagePool == null) {
            throw new NullPointerException("pagePool");
        }
        this.maximumCapacity = maximumCapacity;
        this.pagePool = pagePool;

        currentCapacity = 0;
        writeEnd = 0;

        pages = new ArrayList<>();
    }

    private NioPageBuffer(final NioPageBuffer original) {
        currentCapacity = original.currentCapacity;
        maximumCapacity = original.currentCapacity;
        pagePool = original.pagePool;
        pages = original.pages;
        writeEnd = original.writeEnd;
        readSet = NioPageSet.copy(original.readSet);
        writeSet = NioPageSet.copy(original.writeSet);

        offsets(original.readOffset(), original.writeOffset());

        if (original.currentCapacity < original.writeEnd) {
            truncateOffsetsTo(original.currentCapacity);
            writeEnd = original.currentCapacity;
        }
    }

    @Override
    public int writeEnd() {
        return writeEnd;
    }

    @Override
    public void writeEndUnchecked(final int writeEnd) {
        if (currentCapacity >= writeEnd) {
            truncateOffsetsTo(writeEnd);
        }
        else if (currentCapacity == 0) {
            currentCapacity = pagePool.allocate(pages, writeEnd);
            readSet = new NioPageSet(pages);
            writeSet = new NioPageSet(pages);
        }
        else {
            currentCapacity = pagePool.allocate(pages, currentCapacity - writeEnd);
        }
        this.writeEnd = writeEnd;
    }

    @Override
    public int writeEndMax() {
        return maximumCapacity;
    }

    @Override
    protected Buffer copyUnchecked(final int offset, final int length) {
        final var copy = new NioPageBuffer(length, pagePool);
        copy.setAtUnchecked(0, this, offset, length);
        copy.offsets(readOffset(), writeOffset());
        return copy;
    }

    @Override
    protected Buffer dupeUnchecked() {
        return new NioPageBuffer(this);
    }

    @Override
    protected void getAtUnchecked(final int offset, final byte[] destination, int destinationOffset, int length) {
        var page = readSet.pageAt(offset);
        while (true) {
            final var remainder = Math.min(page.remaining(), length);
            page.get(destination, destinationOffset, remainder);

            destinationOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            page = readSet.nextPage();
        }
    }

    @Override
    protected void getAtUnchecked(final int offset, final BufferWriter destination, int destinationOffset, int length) {
        var page = readSet.pageAt(offset).asReadOnlyBuffer();
        while (true) {
            final var remainder = Math.min(page.remaining(), length);
            destination.setAt(destinationOffset, page.limit(page.position() + remainder));

            destinationOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            page = readSet.nextPage();
        }
    }

    @Override
    protected void getAtUnchecked(final int offset, final ByteBuffer destination) {
        var page = readSet.pageAt(offset).asReadOnlyBuffer();
        while (true) {
            final var remainder = Math.min(page.remaining(), destination.remaining());
            destination.put(page.limit(page.position() + remainder));

            if (!destination.hasRemaining()) {
                break;
            }

            page = readSet.nextPage();
        }
    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        return readSet.pageAt(offset)
            .get();
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        var page = readSet.pageAt(offset);

        if (page.remaining() >= 2) {
            return page.getShort();
        }

        final var byteArray = new byte[2];

        page.get(byteArray, 0, 1);
        page = readSet.nextPage();
        assert page.remaining() >= 1;
        page.get(byteArray, 1, 1);

        return BinaryMath.getS16NeAt(byteArray, 0);
    }

    @Override
    protected int getS32AtUnchecked(final int offset) {
        var page = readSet.pageAt(offset);
        var pageBytesRemaining = page.remaining();

        if (pageBytesRemaining >= 4) {
            return page.getInt();
        }

        final var byteArray = new byte[4];

        page.get(byteArray, 0, pageBytesRemaining);
        page = readSet.nextPage();
        assert page.remaining() >= 4 - pageBytesRemaining;
        page.get(byteArray, pageBytesRemaining, 4 - pageBytesRemaining);

        return BinaryMath.getS32NeAt(byteArray, 0);
    }

    @Override
    protected long getS64AtUnchecked(final int offset) {
        var page = readSet.pageAt(offset);
        var pageBytesRemaining = page.remaining();

        if (pageBytesRemaining >= 8) {
            return page.getLong();
        }

        final var byteArray = new byte[8];

        page.get(byteArray, 0, pageBytesRemaining);
        page = readSet.nextPage();
        assert page.remaining() >= 8 - pageBytesRemaining;
        page.get(byteArray, pageBytesRemaining, 8 - pageBytesRemaining);

        return BinaryMath.getS64NeAt(byteArray, 0);
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, int sourceOffset, int length) {
        var page = writeSet.pageAt(offset);
        while (true) {
            final var remainder = Math.min(page.remaining(), length);
            page.put(source, sourceOffset, remainder);

            sourceOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            page = writeSet.nextPage();
        }
    }

    @Override
    protected void setAtUnchecked(final int offset, final BufferReader source, int sourceOffset, int length) {
        var page = writeSet.pageAt(offset).duplicate();
        while (true) {
            final var remainder = Math.min(page.remaining(), length);
            source.getAt(sourceOffset, page.limit(page.position() + remainder));

            sourceOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            page = writeSet.nextPage();
        }
    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {
        var page = writeSet.pageAt(offset).duplicate();
        final var source0 = source.asReadOnlyBuffer();
        while (true) {
            final var remainder = Math.min(page.remaining(), source0.remaining());
            page.limit(page.position() + remainder)
                .put(source0.limit(source0.position() + remainder));

            if (!source0.hasRemaining()) {
                break;
            }

            page = writeSet.nextPage();
        }
    }

    @Override
    protected void fillAtUnchecked(int offset, final byte value, int length) {
        var page = writeSet.pageAt(offset);
        while (true) {
            var remainder = Math.min(page.remaining(), length);

            if (page.hasArray()) {
                final var offset0 = page.arrayOffset() + offset;
                Arrays.fill(page.array(), offset0, offset0 + remainder, value);
                length -= remainder;
            }
            else {
                while (remainder > 0) {
                    page.put(value);
                    remainder -= 1;
                    length -= 1;
                }
            }

            if (length <= 0) {
                break;
            }

            page = writeSet.nextPage();
        }
    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {
        writeSet.pageAt(offset)
            .put(value);
    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {
        var page = writeSet.pageAt(offset);

        if (page.remaining() >= 2) {
            page.putShort(value);
            return;
        }

        final var byteArray = new byte[2];
        BinaryMath.setS16NeAt(byteArray, 0, value);

        page.put(byteArray[0]);
        page = writeSet.nextPage();
        assert page.remaining() >= 1;
        page.put(byteArray[1]);
    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {
        var page = writeSet.pageAt(offset);
        var pageBytesRemaining = page.remaining();

        if (pageBytesRemaining >= 4) {
            page.putInt(value);
            return;
        }

        final var byteArray = new byte[4];
        BinaryMath.setS32NeAt(byteArray, 0, value);

        page.put(byteArray, 0, pageBytesRemaining);
        page = writeSet.nextPage();
        assert page.remaining() >= 4 - pageBytesRemaining;
        page.put(byteArray, pageBytesRemaining, 4 - pageBytesRemaining);
    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {
        var page = writeSet.pageAt(offset);
        var pageBytesRemaining = page.remaining();

        if (pageBytesRemaining >= 8) {
            page.putLong(value);
            return;
        }

        final var byteArray = new byte[8];
        BinaryMath.setS64NeAt(byteArray, 0, value);

        page.put(byteArray, 0, pageBytesRemaining);
        page = writeSet.nextPage();
        assert page.remaining() >= 8 - pageBytesRemaining;
        page.put(byteArray, pageBytesRemaining, 8 - pageBytesRemaining);
    }

    @Override
    protected void onClose() {
        try {
            pagePool.free(pages);
        }
        finally {
            pages = null;
            pagePool = null;
            readSet.close();
            readSet = null;
            writeSet.close();
            writeSet = null;
        }
    }

    @Override
    public ByteBuffer[] toByteBuffers() {
        return pages.toArray(new ByteBuffer[0]); // TODO: Is this really OK?
    }
}
