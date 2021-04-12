package se.arkalix.io.buf._internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class NioPageSet implements AutoCloseable {
    private ArrayList<ByteBuffer> pages;

    private ByteBuffer page;
    private int pageIndex;
    private int pageStartOffset;
    private int pageStopOffset;

    public NioPageSet(final ArrayList<ByteBuffer> pages) {
        this.pages = pages;
    }

    public ByteBuffer pageAt(final int offset) {
        if (offset >= pageStopOffset) {
            do {
                pageIndex += 1;
                page = pages.get(pageIndex);
                pageStartOffset = pageStopOffset;
                pageStopOffset += page.capacity();
            }
            while (offset >= pageStopOffset);
        }
        else if (offset < pageStartOffset) {
            do {
                pageIndex -= 1;
                page = pages.get(pageIndex);
                pageStopOffset = pageStartOffset;
                pageStartOffset -= page.capacity();
            }
            while (offset < pageStartOffset);
        }
        return page.position(pageStartOffset - offset);
    }

    public ByteBuffer nextPage() {
        pageIndex += 1;
        page = pages.get(pageIndex);
        pageStartOffset = pageStopOffset;
        pageStopOffset += page.capacity();
        return page.position(0);
    }

    @Override
    public void close() {
        pages = null;
    }

    public static NioPageSet copy(final NioPageSet original) {
        if (original == null) {
            return null;
        }
        final var copy = new NioPageSet(original.pages);
        copy.page = original.page;
        copy.pageIndex = original.pageIndex;
        copy.pageStartOffset = original.pageStartOffset;
        copy.pageStopOffset = original.pageStopOffset;
        return copy;
    }
}
