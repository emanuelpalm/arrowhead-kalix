package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

@Internal
public class NioPageBuffer implements Buffer {
    private final boolean isExpanding;

    private NioPagePool pagePool;
    private ArrayList<ByteBuffer> pages;

    private boolean isClosed;

    public NioPageBuffer(final NioPagePool pagePool, final ArrayList<ByteBuffer> pages, final boolean isExpanding) {
        this.pagePool = Objects.requireNonNull(pagePool, "pagePool");
        this.pages = Objects.requireNonNull(pages, "pages");
        this.isExpanding = isExpanding;
    }

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            try {
                pagePool.recycle(pages);
            }
            finally {
                pagePool = null;
                pages = null;
            }
        }
    }

    @Override
    public BufferReader closeAndRead() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        try {
            return new NioPageBufferReader(pagePool, pages);
        }
        finally {
            pagePool = null;
            pages = null;
        }
    }

    @Override
    public BufferWriter closeAndWrite() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
        try {
            return new NioPageBufferWriter(pagePool, pages, isExpanding);
        }
        finally {
            pagePool = null;
            pages = null;
        }
    }

    @Override
    public Buffer copy() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return new NioPageBuffer(pagePool, ByteBuffers.copy(pages), isExpanding);
    }
}
