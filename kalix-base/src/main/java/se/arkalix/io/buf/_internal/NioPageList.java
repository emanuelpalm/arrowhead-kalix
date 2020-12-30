package se.arkalix.io.buf._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.LinkedList;

@Internal
public class NioPageList {
    private final boolean isUsingDirectBuffers;
    private final int pageSize;
    private final int purgeInterval;
    private final LinkedList<ByteBuffer> list = new LinkedList<>();

    private int freeCount = 0;

    public NioPageList(final boolean isUsingDirectBuffers, final int pageSize, final int purgeInterval) {
        this.isUsingDirectBuffers = isUsingDirectBuffers;
        this.pageSize = pageSize;
        this.purgeInterval = purgeInterval;
    }

    public ByteBuffer popOrAllocate() {
        var head = list.poll();
        if (head == null) {
            if (isUsingDirectBuffers) {
                head = ByteBuffer.allocateDirect(pageSize);
            }
            else {
                head = ByteBuffer.allocate(pageSize);
            }
        }
        return head;
    }

    public void pushOrFree(final ByteBuffer page) {
        assert page.capacity() == pageSize;

        list.push(page);

        if (++freeCount >= purgeInterval) {
            freeCount = 0;
            try {
                list.forEach(ByteBuffers::free);
            }
            finally {
                list.clear();
            }
        }
    }
}
