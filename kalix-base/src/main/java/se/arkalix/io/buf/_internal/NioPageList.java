package se.arkalix.io.buf._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

@Internal
public class NioPageList {
    private final boolean isUsingDirectBuffers;
    private final int pageSize;
    private final int pageThreshold;
    private final LinkedList<ByteBuffer> list = new LinkedList<>();

    public NioPageList(final boolean isUsingDirectBuffers, final int pageSize, final int pageThreshold) {
        this.isUsingDirectBuffers = isUsingDirectBuffers;
        this.pageSize = pageSize;
        this.pageThreshold = pageThreshold;
    }

    public ByteBuffer pop() {
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

    public void push(final ByteBuffer page) {
        assert page.capacity() == pageSize;

        list.push(page);
    }
}
