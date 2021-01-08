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
/*
    public int getAt(final int offset, final GatheringByteChannel destination, int length) throws IOException {
        pageAt(offset);
        final int startPageIndex = pageIndex;

        int numberReadOfPages = 1;
        while (true) {
            length -= page.capacity();
            if (length <= 0) {
                page.limit(page.capacity() + length);
                break;
            }
            numberReadOfPages += 1;
            nextPage();
        }
        try {
            return (int) destination.write(pages.toArray(new ByteBuffer[0]), startPageIndex, numberReadOfPages);
        }
        finally {
            page.clear();
        }
    }

    public int setAt(final int offset, final ScatteringByteChannel source, int length) throws IOException {
        pageAt(offset);
        final int startPageIndex = pageIndex;

        int numberWrittenOfPages = 1;
        while (true) {
            length -= page.capacity();
            if (length <= 0) {
                page.limit(page.capacity() + length);
                break;
            }
            numberWrittenOfPages += 1;
            nextPage();
        }
        try {
            return (int) source.read(pages.toArray(new ByteBuffer[0]), startPageIndex, numberWrittenOfPages);
        }
        finally {
            page.clear();
        }
    }*/
}
