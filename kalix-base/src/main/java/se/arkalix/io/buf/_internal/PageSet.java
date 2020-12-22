package se.arkalix.io.buf._internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PageSet implements AutoCloseable {
    private ArrayList<Page> pages;

    private Page page;
    private int pageIndex;
    private int pageStartOffset;
    private int pageStopOffset;

    public PageSet(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public ByteBuffer getPageByteBufferPositionedAt(final int offset) {
        if (offset >= pageStopOffset) {
            do {
                pageIndex += 1;
                page = pages.get(pageIndex);
                pageStartOffset = pageStopOffset;
                pageStopOffset += page.size();
            }
            while (offset >= pageStopOffset);
        }
        else if (offset < pageStartOffset) {
            do {
                pageIndex -= 1;
                page = pages.get(pageIndex);
                pageStopOffset = pageStartOffset;
                pageStartOffset -= page.size();
            }
            while (offset < pageStartOffset);
        }
        return page.byteBuffer
            .position(page.startOffset + (pageStartOffset - offset))
            .limit(page.stopOffset);
    }

    public ByteBuffer nextPageByteBuffer() {
        pageIndex += 1;
        page = pages.get(pageIndex);
        pageStartOffset = pageStopOffset;
        pageStopOffset += page.size();
        return page.byteBuffer
            .position(page.startOffset)
            .limit(page.stopOffset);
    }

    @Override
    public void close() {
        pages = null;
    }
}
