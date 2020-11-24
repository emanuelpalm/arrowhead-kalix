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
    private final ArrayList<Buffer> children;
    private final int childByteCapacity;
    private final int givenCapacity;

    private int byteCapacity = 0;
    private int byteOffset = 0;
    private boolean isClosed = false;

    public PageBufferFixed(final Collection<Buffer> children, final int childByteCapacity) {
        Objects.requireNonNull(children, "children");
        if (childByteCapacity <= 0) {
            throw new IllegalArgumentException("childByteCapacity <= 0");
        }

        this.children = new ArrayList<>(children);
        this.childByteCapacity = childByteCapacity;
        givenCapacity = Math.multiplyExact(children.size(), childByteCapacity);
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
            for (var i = children.size(); i-- > 0; ) {
                final var child = children.get(i);
                child.drop();
            }
            children.clear();
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
            throw new BufferCapacityNotIncreased();
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
