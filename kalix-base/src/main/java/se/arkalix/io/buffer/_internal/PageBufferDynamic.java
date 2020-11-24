package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.*;
import se.arkalix.util.annotation.Internal;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Internal
public class PageBufferDynamic implements Buffer {
    private final ArrayList<Buffer> children = new ArrayList<>();
    private final int childByteCapacity;
    private final BufferPageAllocator bufferPageAllocator;

    private int byteCapacity = 0;
    private int byteOffset = 0;
    private boolean isClosed = false;

    public PageBufferDynamic(final BufferPageAllocator bufferPageAllocator) {
        this.bufferPageAllocator = Objects.requireNonNull(bufferPageAllocator, "pageAllocator");
        childByteCapacity = bufferPageAllocator.bufferCapacity();
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
        var capacityDifference = capacity - children.size() * childByteCapacity;
        if (capacityDifference > 0) {
            try {
                children.addAll(bufferPageAllocator.allocateMemory(capacityDifference));
            }
            catch (final BufferAllocationFailed exception) {
                throw new BufferCapacityNotIncreased(exception);
            }
        }
        else if (capacityDifference < 0) {
            for (var i = children.size(); i-- > 0; ) {
                final var child = children.get(i);
                capacityDifference += child.capacity();
                if (capacityDifference > 0) {
                    break;
                }
                child.drop();
            }
        }
        this.byteCapacity = capacity;
    }

    @Override
    public void drop() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            for (var i = children.size(); i-- > 0; ) {
                final var child = children.get(i);
                child.drop();
                byteCapacity -= childByteCapacity;
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

        final var childIndex = offset / childByteCapacity;
        final var childByteOffset = offset - childIndex * childByteCapacity;

        children.get(childIndex).putByte(childByteOffset, b);
    }

    @Override
    public void putBytes(final int offset, final byte[] source, int sourceOffset, int length) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || sourceOffset < 0 || length < 0 || length > source.length - sourceOffset) {
            throw new IndexOutOfBoundsException();
        }
        if (length > space()) {
            capacity(offset + length);
        }

        var childIndex = offset / childByteCapacity;
        final var childLast = (offset + length) / childByteCapacity;

        final var childByteOffset = offset - childIndex * childByteCapacity;
        var childByteLength = Math.min(childByteCapacity - childByteOffset, length);

        children.get(childIndex).putBytes(childByteOffset, source, sourceOffset, childByteLength);

        while (++childIndex <= childLast) {
            sourceOffset += childByteLength;
            length -= childByteLength;
            childByteLength = Math.min(childByteCapacity, length);

            children.get(childIndex).putBytes(0, source, sourceOffset, childByteLength);
        }
    }

    @Override
    public BufferView view() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            return new PageBufferView(children.stream()
                .map(Buffer::view)
                .collect(Collectors.toUnmodifiableList()),
                childByteCapacity, 0, byteOffset
            );
        }
        finally {
            isClosed = true;
        }
    }
}
