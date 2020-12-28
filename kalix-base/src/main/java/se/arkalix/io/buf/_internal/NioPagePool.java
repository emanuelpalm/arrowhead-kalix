package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferAllocator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class NioPagePool implements BufferAllocator {
    private final boolean isDirect;
    private final LinkedList<ByteBuffer> freeList = new LinkedList<>();

    public static NioPagePool create() {
        return new NioPagePool(false);
    }

    public static NioPagePool createDirect() {
        return new NioPagePool(true);
    }

    private NioPagePool(final boolean isDirect) {
        this.isDirect = isDirect;
    }

    public int allocate(final ArrayList<ByteBuffer> destination, final int sizeInBytes) {
        return -1;
    }

    public void free(final ArrayList<ByteBuffer> pages) {

    }

    @Override
    public Buffer allocate(final int initialCapacity, final int maximumCapacity) {
        return null;
    }
}
