package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.BufferIsClosed;
import se.arkalix.io.buffer.BufferView;
import se.arkalix.util.annotation.Internal;

import java.util.List;
import java.util.stream.Collectors;

@Internal
public class PageBufferView implements BufferView {
    private final List<BufferView> children;
    private final int childBufferSize;
    private final int byteSize;

    private boolean isClosed;
    private int byteOffset;

    public PageBufferView(
        final List<BufferView> children,
        final int childBufferSize,
        final int byteOffset,
        final int byteSize
    )
    {
        this.children = children;
        this.childBufferSize = childBufferSize;
        this.byteOffset = byteOffset;
        this.byteSize = byteSize;
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        try {
            children.forEach(BufferView::close);
        }
        finally {
            isClosed = true;
        }
    }

    @Override
    public BufferView dupe() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return new PageBufferView(children.stream()
            .map(BufferView::dupe)
            .collect(Collectors.toUnmodifiableList()),
            childBufferSize, byteOffset, byteSize);
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
        if (offset < 0 || offset > byteSize) {
            throw new IndexOutOfBoundsException();
        }
        this.byteOffset = offset;
    }

    @Override
    public byte getByte(final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || offset > byteSize) {
            throw new IndexOutOfBoundsException();
        }

        final var childIndex = offset / childBufferSize;
        final var childByteOffset = offset - childIndex * childBufferSize;

        return children.get(childIndex).getByte(childByteOffset);
    }

    @Override
    public void getBytes(final int offset, final byte[] target, int targetOffset, int length) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || targetOffset < 0 || length < 0 ||
            length > target.length - targetOffset || length > remainder())
        {
            throw new IndexOutOfBoundsException();
        }

        var childIndex = offset / childBufferSize;
        final var childLast = (offset + length) / childBufferSize;

        final var childByteOffset = offset - childIndex * childBufferSize;
        var childByteLength = Math.min(childBufferSize - childByteOffset, length);

        children.get(childIndex).getBytes(childByteOffset, target, targetOffset, childByteLength);

        while (++childIndex <= childLast) {
            targetOffset += childByteLength;
            length -= childByteLength;
            childByteLength = Math.min(childBufferSize, length);

            children.get(childIndex).getBytes(0, target, targetOffset, childByteLength);
        }
    }

    @Override
    public int size() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return byteSize;
    }
}
