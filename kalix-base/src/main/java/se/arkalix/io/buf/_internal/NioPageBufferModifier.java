package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferIsClosed;
import se.arkalix.io.mem.Limit;
import se.arkalix.io.mem.Offset;
import se.arkalix.io.mem.Read;
import se.arkalix.io.mem.Write;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;

@Internal
public abstract class NioPageBufferModifier implements AutoCloseable, Limit, Offset, Write {
    private final boolean isExpanding;

    private NioPagePool pagePool;
    private ArrayList<ByteBuffer> pages;

    private boolean isClosed = false;

    private int byteLimit = 0;
    private int byteOffset = 0;

    private ByteBuffer pageCurrent;
    private int pageOffset = 0;


    protected NioPageBufferModifier(
        final NioPagePool pagePool,
        final ArrayList<ByteBuffer> pages,
        final boolean isExpanding
    ) {
        this.pagePool = Objects.requireNonNull(pagePool, "pagePool");
        this.pages = Objects.requireNonNull(pages, "pages");
        this.isExpanding = isExpanding;

        for (final var page : pages) {
            byteLimit += page.limit();
        }
        if (!pages.isEmpty()) {
            pageCurrent = pages.get(0);
        }
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

    protected <T> T closeAnd(final BiFunction<NioPagePool, ArrayList<ByteBuffer>, T> action) {
        ensureIsOpen();

        isClosed = true;
        try {
            return action.apply(pagePool, pages);
        }
        finally {
            pagePool = null;
            pages = null;
        }
    }

    public Buffer copy() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return new NioPageBuffer(pagePool, ByteBuffers.copy(pages), isExpanding);
    }

    protected ArrayList<ByteBuffer> duplicatePagesUnsafe() {
        return ByteBuffers.duplicate(pages);
    }

    protected void ensureIsOpen() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
    }

    protected boolean isClosed() {
        return isClosed;
    }

    @Override
    public int limit() {
        ensureIsOpen();

        return byteLimit;
    }

    @Override
    public int offset() {
        ensureIsOpen();

        return byteOffset;
    }

    @Override
    public void offset(final int offset) {
        ensureIsOpen();

        if (offset < 0 || offset > byteLimit) {
            if (isExpanding) {
                byteLimit = pagePool.allocate(pages, offset() - offset);
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
        }
        else if (offset < byteOffset) {
            do {
                pageCurrent = pages.get(pageOffset--);
                byteOffset -= pageCurrent.limit();
            }
            while (offset < byteOffset);
        }
        pageCurrent = pages.get(pageOffset);
        pageCurrent.position(offset - byteOffset);
    }

    protected NioPagePool pagePoolUnsafe() {
        return pagePool;
    }

    @Override
    public int write(final Read source) {
        if (source == null) {
            throw new NullPointerException("source");
        }

        if (pageCurrent == null) {
            return 0;
        }

        final int byteOffsetOriginal = byteOffset;

        if (source instanceof NioPageBufferReader) {
            writeUnsafe((NioPageBufferModifier) source);
        }
        else if (source instanceof ByteArrayBufferReader) {
            writeUnsafe((ByteArrayBufferReader) source);
        }
        else {
            writeUnsafe(source);
        }

        return byteOffset - byteOffsetOriginal;
    }

    private void writeUnsafe(final NioPageBufferModifier source) {
        if (source.pageCurrent == null) {
            return;
        }

        while (true) {
            if (pageCurrent.remaining() < source.pageCurrent.remaining()) {
                final var limit = pageCurrent.remaining();

                final var ro = source.pageCurrent.asReadOnlyBuffer();
                ro.limit(limit);
                pageCurrent.put(ro);

                byteOffset += limit;
                source.byteOffset += limit;

                if (pageOffset == pages.size()) {
                    return;
                }
                pageCurrent = pages.get(pageOffset++);
                pageCurrent.rewind();
            }

            if (pageCurrent.remaining() > source.pageCurrent.remaining()) {
                final var limit = source.pageCurrent.remaining();

                pageCurrent.put(source.pageCurrent);

                byteOffset += limit;
                source.byteOffset += limit;

                if (source.pageOffset == source.pages.size()) {
                    return;
                }
                source.pageCurrent = source.pages.get(source.pageOffset++);
                source.pageCurrent.rewind();
            }

            if (pageCurrent.remaining() == source.pageCurrent.remaining()) {
                final var limit = pageCurrent.remaining();

                pageCurrent.put(source.pageCurrent);

                byteOffset += limit;
                source.byteOffset += limit;

                boolean isDone = false;

                if (pageOffset < pages.size()) {
                    pageCurrent = pages.get(pageOffset++);
                    pageCurrent.rewind();
                }
                else {
                    isDone = true;
                }

                if (source.pageOffset < source.pages.size()) {
                    source.pageCurrent = source.pages.get(source.pageOffset++);
                    source.pageCurrent.rewind();
                }
                else {
                    isDone = true;
                }

                if (isDone) {
                    return;
                }
            }
        }
    }

    private void writeUnsafe(final ByteArrayBufferReader source) {
        int bytesRead;

        while (true) {
            final var remaining = pageCurrent.remaining();

            bytesRead = source.read(pageCurrent);
            byteOffset += bytesRead;

            if (bytesRead < remaining || pageOffset == pages.size()) {
                break;
            }

            pageCurrent = pages.get(pageOffset++);
            pageCurrent.rewind();
        }
    }

    private void writeUnsafe(final Read source) {
        byte[] buffer = null;
        int bytesRead;

        while (true) {
            final var remaining = pageCurrent.remaining();

            if (pageCurrent.hasArray()) {
                final var array = pageCurrent.array();
                final var offset = pageCurrent.arrayOffset();

                bytesRead = source.read(array, offset, remaining);
                byteOffset += bytesRead;
            }
            else {
                if (buffer == null) {
                    buffer = new byte[2048]; // Intermediate buffer used as a last resort.
                }
                do {
                    bytesRead = source.read(buffer, 0, Math.min(buffer.length, remaining));
                    byteOffset += bytesRead;
                } while (bytesRead >= buffer.length);
            }

            if (bytesRead < remaining || pageOffset == pages.size()) {
                break;
            }

            pageCurrent = pages.get(pageOffset++);
            pageCurrent.rewind();
        }
    }
}
