package se.arkalix.io.buf._internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PageBlock {
    private final Page[] pages = new Page[32];

    private int freePageCount;

    public PageBlock(final int pageSize) {
        final var byteBuffer = ByteBuffer.allocateDirect(pageSize * pages.length);
        for (int i = pages.length; i-- != 0; ) {
            pages[i] = new Page(byteBuffer, i * pageSize, (i + 1) * pageSize);
        }
    }

    public int allocatePages(final ArrayList<Page> destination, final int pageCount) {
        var i = pageCount;
        for (; freePageCount > 0 && i > 0; --i) {
            destination.add(pages[--freePageCount]);
        }
        return pageCount - i;
    }

    public int freePageCount() {
        return freePageCount;
    }
}
