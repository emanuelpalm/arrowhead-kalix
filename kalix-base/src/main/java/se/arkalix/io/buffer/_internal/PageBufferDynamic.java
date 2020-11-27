package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.*;
import se.arkalix.util.annotation.Internal;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Internal
public class PageBufferDynamic implements Buffer {
    private final ArrayList<Buffer> pages = new ArrayList<>();
    private final int pageByteCapacity;
    private final PageAllocator pageAllocator;

    private int byteCapacity = 0;
    private int byteOffset = 0;
    private boolean isClosed = false;

    public PageBufferDynamic(final PageAllocator pageAllocator) {
        this.pageAllocator = Objects.requireNonNull(pageAllocator, "bufferPageAllocator");
        pageByteCapacity = pageAllocator.pageSize();
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
        var capacityDifference = capacity - pages.size() * pageByteCapacity;
        if (capacityDifference > 0) {
            try {
                pages.addAll(pageAllocator.allocateBytes(capacityDifference));
            }
            catch (final BufferAllocationFailed exception) {
                throw new BufferCapacityNotIncreased(exception);
            }
        }
        else if (capacityDifference < 0) {
            for (var i = pages.size(); i-- > 0; ) {
                final var page = pages.get(i);
                capacityDifference += page.capacity();
                if (capacityDifference > 0) {
                    break;
                }
                page.close();
            }
        }
        this.byteCapacity = capacity;
    }

    @Override
    public void close() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            for (var i = pages.size(); i-- > 0; ) {
                final var page = pages.get(i);
                page.close();
                byteCapacity -= pageByteCapacity;
            }
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
            capacity(offset + 1);
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
            capacity(offset + 1);
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
            capacity(offset + length);
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
    public BufferReader read() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            return new PageBufferReader(pages.stream()
                .map(Buffer::read)
                .collect(Collectors.toUnmodifiableList()),
                pageByteCapacity, 0, byteOffset
            );
        }
        finally {
            isClosed = true;
        }
    }
}
