package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util._internal.BinaryMath;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BufferPaged extends BufferBase {
    private final int maximumCapacity;

    private int currentCapacity = 0;
    private int writeEnd = 0;

    private PageCache pageCache;
    private ArrayList<Page> pages = new ArrayList<>();
    private PageSet readSet = null;
    private PageSet writeSet = null;

    public BufferPaged(final int maximumCapacity, final PageCache pageCache) {
        if (maximumCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (pageCache == null) {
            throw new NullPointerException("pageCache");
        }
        this.maximumCapacity = maximumCapacity;
        this.pageCache = pageCache;
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
        if (currentCapacity >= writeEnd) {
            truncateOffsetsTo(writeEnd);
        }
        else if (currentCapacity == 0) {
            currentCapacity = pageCache.allocate(pages, writeEnd);
            readSet = new PageSet(pages);
            writeSet = new PageSet(pages);
        }
        else {
            currentCapacity = pageCache.allocate(pages, currentCapacity - writeEnd);
        }
        this.writeEnd = writeEnd;
    }

    @Override
    public int writeEndMax() {
        return maximumCapacity;
    }

    @Override
    protected Buffer copyUnchecked(final int offset, final int length) {
        throw new UnsupportedOperationException("not implemented"); // TODO.
    }

    @Override
    protected Buffer dupeUnchecked(final int offset, final int length) {
        throw new UnsupportedOperationException("not implemented"); // TODO.
    }

    @Override
    protected void getAtUnchecked(final int offset, final byte[] destination, int destinationOffset, int length) {
        var pageByteBuffer = readSet.getPageByteBufferPositionedAt(offset);
        while (true) {
            final var remainder = Math.min(pageByteBuffer.remaining(), length);
            pageByteBuffer.get(destination, destinationOffset, remainder);

            destinationOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            pageByteBuffer = readSet.nextPageByteBuffer();
        }
    }

    @Override
    protected void getAtUnchecked(final int offset, final BufferWriter destination, int destinationOffset, int length) {
        var pageByteBuffer = readSet.getPageByteBufferPositionedAt(offset);
        while (true) {
            final var remainder = Math.min(pageByteBuffer.remaining(), length);
            destination.setAt(destinationOffset, pageByteBuffer.limit(pageByteBuffer.position() + remainder));

            destinationOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            pageByteBuffer = readSet.nextPageByteBuffer();
        }
    }

    @Override
    protected void getAtUnchecked(final int offset, final ByteBuffer destination) {
        final var destinationDuplicate = destination.duplicate();
        var pageByteBuffer = readSet.getPageByteBufferPositionedAt(offset);
        while (true) {
            final var remainder = Math.min(pageByteBuffer.remaining(), destination.remaining());
            destinationDuplicate
                .limit(destinationDuplicate.position() + remainder)
                .put(pageByteBuffer.limit(pageByteBuffer.position() + remainder));

            if (!destinationDuplicate.hasRemaining()) {
                break;
            }

            pageByteBuffer = readSet.nextPageByteBuffer();
        }
    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        return readSet.getPageByteBufferPositionedAt(offset)
            .get();
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        var pageByteBuffer = readSet.getPageByteBufferPositionedAt(offset);

        if (pageByteBuffer.remaining() >= 2) {
            return pageByteBuffer.getShort();
        }

        final var byteArray = new byte[2];

        pageByteBuffer.get(byteArray, 0, 1);
        pageByteBuffer = readSet.nextPageByteBuffer();
        pageByteBuffer.get(byteArray, 1, 1);

        return BinaryMath.getS16NeAt(byteArray, 0);
    }

    @Override
    protected int getS32AtUnchecked(final int offset) {
        var pageByteBuffer = readSet.getPageByteBufferPositionedAt(offset);
        var pageBytesRemaining = pageByteBuffer.remaining();

        if (pageBytesRemaining >= 4) {
            return pageByteBuffer.getInt();
        }

        final var byteArray = new byte[4];

        pageByteBuffer.get(byteArray, 0, pageBytesRemaining);
        pageByteBuffer = readSet.nextPageByteBuffer();
        pageByteBuffer.get(byteArray, pageBytesRemaining, 4 - pageBytesRemaining);

        return BinaryMath.getS32NeAt(byteArray, 0);
    }

    @Override
    protected long getS64AtUnchecked(final int offset) {
        var pageByteBuffer = readSet.getPageByteBufferPositionedAt(offset);
        var pageBytesRemaining = pageByteBuffer.remaining();

        if (pageBytesRemaining >= 8) {
            return pageByteBuffer.getLong();
        }

        final var byteArray = new byte[8];

        pageByteBuffer.get(byteArray, 0, pageBytesRemaining);

        pageByteBuffer = readSet.nextPageByteBuffer();
        pageByteBuffer.get(byteArray, pageBytesRemaining, 8 - pageBytesRemaining);

        return BinaryMath.getS64NeAt(byteArray, 0);
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, int sourceOffset, int length) {
        var pageByteBuffer = writeSet.getPageByteBufferPositionedAt(offset);
        while (true) {
            final var remainder = Math.min(pageByteBuffer.remaining(), length);
            pageByteBuffer.put(source, sourceOffset, remainder);

            sourceOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            pageByteBuffer = writeSet.nextPageByteBuffer();
        }
    }

    @Override
    protected void setAtUnchecked(final int offset, final BufferReader source, int sourceOffset, int length) {
        var pageByteBuffer = writeSet.getPageByteBufferPositionedAt(offset);
        while (true) {
            final var remainder = Math.min(pageByteBuffer.remaining(), length);
            source.getAt(sourceOffset, pageByteBuffer.limit(pageByteBuffer.position() + remainder));

            sourceOffset += remainder;
            length -= remainder;

            if (length <= 0) {
                break;
            }

            pageByteBuffer = writeSet.nextPageByteBuffer();
        }
    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {
        final var sourceDuplicate = source.duplicate();
        var pageByteBuffer = writeSet.getPageByteBufferPositionedAt(offset);
        while (true) {
            final var remainder = Math.min(pageByteBuffer.remaining(), source.remaining());
            pageByteBuffer.limit(pageByteBuffer.position() + remainder)
                .put(sourceDuplicate.limit(sourceDuplicate.position() + remainder));

            if (!sourceDuplicate.hasRemaining()) {
                break;
            }

            pageByteBuffer = writeSet.nextPageByteBuffer();
        }
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte value, final int length) {
        throw new UnsupportedOperationException("not implemented"); // TODO.
    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {
        writeSet.getPageByteBufferPositionedAt(offset).put(value);
    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {
        throw new UnsupportedOperationException("not implemented"); // TODO.
    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {
        throw new UnsupportedOperationException("not implemented"); // TODO.
    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {
        throw new UnsupportedOperationException("not implemented"); // TODO.
    }

    @Override
    protected void onClose() {
        try {
            pageCache.free(pages);
        }
        finally {
            pages = null;
            pageCache = null;
            readSet.close();
            readSet = null;
            writeSet.close();
            writeSet = null;
        }
    }
}
