package se.arkalix.io.buf._internal;

import java.util.ArrayList;

public class PageCache {
    private final PagePool pool;

    private final PageBlock[] stack = new PageBlock[32];
    private int height = 0;

    public PageCache(final PagePool pool) {
        this.pool = pool;
    }

    public int allocate(final ArrayList<Page> destination, final int sizeInBytes) {
        return -1;
    }

    public void free(final ArrayList<Page> pages) {

    }
}
