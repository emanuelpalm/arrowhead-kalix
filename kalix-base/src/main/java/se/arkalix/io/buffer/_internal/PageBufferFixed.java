package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferCapacityNotIncreased;
import se.arkalix.io.buffer.BufferIsClosed;
import se.arkalix.io.buffer.BufferView;
import se.arkalix.util.annotation.Internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Internal
public class PageBufferFixed implements Buffer {
    private final ArrayList<Buffer> pages;
    private final int pageByteCapacity;
    private final int givenCapacity;

    private int byteCapacity = 0;
    private int byteOffset = 0;
    private boolean isClosed = false;

    public PageBufferFixed(final Collection<Buffer> pages, final int pageByteCapacity) {
        Objects.requireNonNull(pages, "children");
        if (pageByteCapacity <= 0) {
            throw new IllegalArgumentException("childByteCapacity <= 0");
        }

        this.pages = new ArrayList<>(pages);
        this.pageByteCapacity = pageByteCapacity;
        givenCapacity = Math.multiplyExact(pages.size(), pageByteCapacity);
    }

    @Override
    public int capacity() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return byteCapacity;
    }

    @Override
    public void capacity(final int capacity) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (capacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (capacity > givenCapacity) {
            throw new BufferCapacityNotIncreased();
        }
        this.byteCapacity = capacity;
    }

    @Override
    public void drop() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            pages.forEach(Buffer::drop);
            pages.clear();
        }
        finally {
            isClosed = true;
        }
    }

    @Override
    public int offset() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return byteOffset;
    }

    @Override
    public void offset(final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset >= byteCapacity) {
            throw new BufferCapacityNotIncreased();
        }
        this.byteOffset = offset;
    }

    @Override
    public void putByte(final int offset, final byte b) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset >= byteCapacity) {
            throw new BufferCapacityNotIncreased();
        }

        final var pageIndex = offset / pageByteCapacity;
        final var pageByteOffset = offset - pageIndex * pageByteCapacity;

        pages.get(pageIndex).putByte(pageByteOffset, b);
    }

    @Override
    public void putBytes(final int offset, final byte[] source, int sourceOffset, int length) {
        Objects.requireNonNull(source, "source");
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || sourceOffset < 0 || length < 0 || length > source.length - sourceOffset) {
            throw new IndexOutOfBoundsException();
        }
        if (length > space()) {
            throw new BufferCapacityNotIncreased();
        }

        var pageIndex = offset / pageByteCapacity;
        final var pageLast = (offset + length) / pageByteCapacity;

        final var pageByteOffset = offset - pageIndex * pageByteCapacity;
        var pageByteLength = Math.min(pageByteCapacity - pageByteOffset, length);

        pages.get(pageIndex).putBytes(pageByteOffset, source, sourceOffset, pageByteLength);

        while (++pageIndex <= pageLast) {
            sourceOffset += pageByteLength;
            length -= pageByteLength;
            pageByteLength = Math.min(pageByteCapacity, length);

            pages.get(pageIndex).putBytes(0, source, sourceOffset, pageByteLength);
        }
    }

    @Override
    public BufferView view() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            return new PageBufferView(pages.stream()
                .map(Buffer::view)
                .collect(Collectors.toUnmodifiableList()),
                pageByteCapacity, 0, byteOffset
            );
        }
        finally {
            isClosed = true;
        }
    }
}
