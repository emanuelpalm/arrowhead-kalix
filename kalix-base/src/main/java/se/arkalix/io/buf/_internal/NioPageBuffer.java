package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.*;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

@Internal
public class NioPageBuffer implements Buffer {
    private final NioBuffer buf = NioBuffer.allocate(8);
    private final boolean isExpanding;

    private ByteBufferPool pagePool;
    private ArrayList<ByteBuffer> pages;

    private int byteLimit = 0;
    private int byteOffset = 0;

    private ByteBuffer pageCurrent;
    private int pageOffset = 0;

    private boolean isClosed;

    public NioPageBuffer(final ByteBufferPool pagePool, final ArrayList<ByteBuffer> pages, final boolean isExpanding) {
        this.pagePool = Objects.requireNonNull(pagePool, "pagePool");
        this.pages = Objects.requireNonNull(pages, "pages");
        this.isExpanding = isExpanding;

        if (pages.isEmpty()) {
            throw new IllegalArgumentException("pages is empty");
        }

        for (final var page : pages) {
            byteLimit += page.limit();
        }
        pageCurrent = pages.get(0);
    }

    @Override
    public int length() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return byteLimit;
    }

    private void offset(final int offset) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > byteLimit) {
            if (isExpanding) {
                byteLimit = pagePool.allocate(pages, byteOffset - offset);
            }
            else {
                throw new IndexOutOfBoundsException();
            }
        }

        if (offset > pageCurrent.limit()) {
            do {
                pageCurrent = pages.get(pageOffset++);
                byteOffset += pageCurrent.limit();
            }
            while (offset > pageCurrent.limit());
            pageCurrent = pages.get(pageOffset);
        }
        else if (offset < byteOffset) {
            do {
                pageCurrent = pages.get(pageOffset--);
                byteOffset -= pageCurrent.limit();
            }
            while (offset < byteOffset);
            pageCurrent = pages.get(pageOffset);
        }
        pageCurrent.position(offset - byteOffset);
    }

    @Override
    public float getFloatAt(final int offset) {
        putAt(offset, buf, 0, Float.BYTES);
        return buf.getFloatAt(0);
    }

    @Override
    public double getDoubleAt(final int offset) {
        putAt(offset, buf, 0, Double.BYTES);
        return buf.getDoubleAt(0);
    }

    @Override
    public byte getByteAt(final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        offset(offset);
        return pageCurrent.get();
    }

    @Override
    public short getShortAt(final int offset) {
        putAt(offset, buf, 0, Short.BYTES);
        return buf.getShortAt(0);
    }

    @Override
    public int getIntAt(final int offset) {
        putAt(offset, buf, 0, Integer.BYTES);
        return buf.getIntAt(0);
    }

    @Override
    public long getLongAt(final int offset) {
        putAt(offset, buf, 0, Long.BYTES);
        return buf.getLongAt(0);
    }

    @Override
    public void putAt(final int offset, final Buffer source, final int sourceOffset, final int length) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        offset(offset);
        // TODO
    }

    @Override
    public void setFloatAt(final int offset, final float value) {
        buf.setFloatAt(0, value);
        putAt(offset, buf, 0, Float.BYTES);
    }

    @Override
    public void setDoubleAt(final int offset, final double value) {
        buf.setDoubleAt(0, value);
        putAt(offset, buf, 0, Double.BYTES);
    }

    @Override
    public void setByteAt(final int offset, final byte value) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        offset(offset);
        pageCurrent.put(value);
    }

    @Override
    public void SetShortAt(final int offset, final short value) {
        buf.SetShortAt(0, value);
        putAt(offset, buf, 0, Short.BYTES);
    }

    @Override
    public void setIntAt(final int offset, final int value) {
        buf.setIntAt(0, value);
        putAt(offset, buf, 0, Integer.BYTES);
    }

    @Override
    public void setLongAt(final int offset, final long value) {
        buf.setLongAt(0, value);
        putAt(offset, buf, 0, Long.BYTES);
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
}
