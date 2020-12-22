package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BufferPaged extends BufferBase {
    private final int maximumCapacity;

    private int currentCapacity = 0;
    private int writeEnd = 0;

    private PageCache pageCache;
    private ArrayList<Page> pages = new ArrayList<>();
    private PageCursor readCursor = null;
    private PageCursor writeCursor = null;

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
            readCursor = new PageCursor();
            writeCursor = new PageCursor();
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
    protected void getAtUnchecked(int offset, final byte[] destination, int destinationOffset, int length) {
        while (length > 0) {
            readCursor.moveToPageAt(offset);

            final var pageRemainder = readCursor.bytesRemainingInCurrentPage();
            final var pageLength = Math.min(pageRemainder, length);

            readCursor.page.buffer.duplicate()
                .position(readCursor.innerOffset)
                .get(destination, destinationOffset, pageLength);

            offset += pageLength;
            destinationOffset += pageLength;
            length -= pageLength;
        }
    }

    @Override
    protected void getAtUnchecked(int offset, final BufferWriter destination, int destinationOffset, int length) {
        while (length > 0) {
            readCursor.moveToPageAt(offset);

            final var pageRemainder = readCursor.bytesRemainingInCurrentPage();
            final var pageLength = Math.min(pageRemainder, length);

            destination.setAt(destinationOffset, readCursor.page.buffer.duplicate()
                .position(readCursor.innerOffset)
                .limit(readCursor.innerOffset + pageLength));

            offset += pageLength;
            destinationOffset += pageLength;
            length -= pageLength;
        }
    }

    @Override
    protected void getAtUnchecked(int offset, final ByteBuffer destination) {
        while (destination.remaining() > 0) {
            readCursor.moveToPageAt(offset);

            final var pageRemainder = readCursor.bytesRemainingInCurrentPage();
            final var pageLength = Math.min(pageRemainder, destination.remaining());

            destination.put(readCursor.page.buffer.duplicate()
                .position(readCursor.innerOffset)
                .limit(readCursor.innerOffset + pageLength));

            offset += pageLength;
        }
    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        readCursor.moveToPageAt(offset);
        return readCursor.page.buffer.get(readCursor.innerOffset++);
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected int getS32AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected long getS64AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, final int sourceOffset, final int length) {

    }

    @Override
    protected void setAtUnchecked(
        final int offset,
        final BufferReader source,
        final int sourceOffset,
        final int length
    ) {

    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {

    }

    @Override
    protected void setAtUnchecked(final int offset, final byte value, final int length) {

    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {

    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {

    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {

    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {

    }

    @Override
    protected void onClose() {
        try {
            pageCache.free(pages);
        }
        finally {
            pageCache = null;
        }
    }

    public class PageCursor {
        private Page page;
        private int pageIndex;
        private int innerOffset;
        private int outerStartOffset;
        private int outerStopOffset;

        public PageCursor() {
            page = pages.get(0);
            pageIndex = 0;
            innerOffset = 0;
            outerStartOffset = 0;
            outerStopOffset = page.size();
        }

        public void moveToPageAt(final int offset) {
            if (offset >= outerStopOffset) {
                do {
                    pageIndex += 1;
                    page = pages.get(pageIndex);
                    outerStartOffset = outerStopOffset;
                    outerStopOffset += page.size();
                }
                while (offset >= outerStopOffset);
            }
            else if (offset < outerStartOffset) {
                do {
                    pageIndex -= 1;
                    page = pages.get(pageIndex);
                    outerStopOffset = outerStartOffset;
                    outerStartOffset -= page.size();
                }
                while (offset < outerStartOffset);
            }
            innerOffset = outerStartOffset - offset;
        }

        public int bytesRemainingInCurrentPage() {
            return page.size() - innerOffset;
        }
    }
}
