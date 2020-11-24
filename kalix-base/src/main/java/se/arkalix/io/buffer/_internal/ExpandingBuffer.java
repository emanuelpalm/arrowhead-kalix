package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.*;
import se.arkalix.util.annotation.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Internal
public class ExpandingBuffer implements Buffer {
    private final ArrayList<Buffer> children = new ArrayList<>();
    private final int childByteCapacity;
    private final FixedSizeBufferAllocator fixedSizeBufferAllocator;

    private int byteCapacity = 0;
    private int byteOffset = 0;
    private boolean isClosed = false;

    public ExpandingBuffer(final FixedSizeBufferAllocator fixedSizeBufferAllocator) {
        this.fixedSizeBufferAllocator = Objects.requireNonNull(fixedSizeBufferAllocator, "pageAllocator");
        childByteCapacity = fixedSizeBufferAllocator.bufferCapacity();
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
                final var n = Math.ceil(((double) capacityDifference) / childByteCapacity);
                children.addAll(fixedSizeBufferAllocator.allocate((int) n));
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

        final var childOffset = offset / childByteCapacity;
        final var childByteOffset = offset - childOffset * childByteCapacity;

        children.get(childOffset).putByte(childByteOffset, b);
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

        var childOffset = offset / childByteCapacity;
        final var childLast = (offset + length) / childByteCapacity;

        final var childByteOffset = offset - childOffset * childByteCapacity;
        var childByteLength = Math.min(childByteCapacity - childByteOffset, length);

        children.get(childOffset).putBytes(childByteOffset, source, sourceOffset, childByteLength);

        while (++childOffset <= childLast) {
            sourceOffset += childByteLength;
            length -= childByteLength;
            childByteLength = Math.min(childByteCapacity, length);

            children.get(childOffset).putBytes(0, source, sourceOffset, childByteLength);
        }
    }

    @Override
    public BufferView view() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            return new View(children.stream()
                .map(Buffer::view)
                .collect(Collectors.toUnmodifiableList()),
                childByteCapacity, 0, byteOffset
            );
        }
        finally {
            isClosed = true;
        }
    }

    @Override
    public void writeByte(final byte b) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (byteOffset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (byteOffset >= byteCapacity) {
            capacity(byteOffset + 1);
        }

        final var childOffset = byteOffset / childByteCapacity;
        final var childByteOffset = byteOffset++ - childOffset * childByteCapacity;

        children.get(childOffset).putByte(childByteOffset, b);
    }

    @Override
    public void writeBytes(final byte[] source, int sourceOffset, int length) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (sourceOffset < 0 || length < 0 || length > source.length - sourceOffset) {
            throw new IndexOutOfBoundsException();
        }
        if (length > space()) {
            capacity(byteOffset + length);
        }

        var childOffset = byteOffset / childByteCapacity;
        final var childLast = (byteOffset + length) / childByteCapacity;

        final var childByteOffset = byteOffset - childOffset * childByteCapacity;
        var childByteLength = Math.min(childByteCapacity - childByteOffset, length);

        children.get(childOffset).putBytes(childByteOffset, source, sourceOffset, childByteLength);
        byteOffset += childByteLength;

        while (++childOffset <= childLast) {
            sourceOffset += childByteLength;
            length -= childByteLength;
            childByteLength = Math.min(childByteCapacity, length);

            children.get(childOffset).putBytes(0, source, sourceOffset, childByteLength);
            byteOffset += childByteLength;
        }
    }

    private static class View implements BufferView {
        private final List<BufferView> children;
        private final int childBufferSize;
        private final int byteSize;

        private boolean isClosed;
        private int byteOffset;

        public View(
            final List<BufferView> children,
            final int childBufferSize,
            final int byteOffset,
            final int byteSize
        ) {
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
            return new View(children.stream()
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

            final var childOffset = offset / childBufferSize;
            final var childByteOffset = offset - childOffset * childBufferSize;

            return children.get(childOffset).getByte(childByteOffset);
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

            var childOffset = offset / childBufferSize;
            final var childLast = (offset + length) / childBufferSize;

            final var childByteOffset = offset - childOffset * childBufferSize;
            var childByteLength = Math.min(childBufferSize - childByteOffset, length);

            children.get(childOffset).getBytes(childByteOffset, target, targetOffset, childByteLength);

            while (++childOffset <= childLast) {
                targetOffset += childByteLength;
                length -= childByteLength;
                childByteLength = Math.min(childBufferSize, length);

                children.get(childOffset).getBytes(0, target, targetOffset, childByteLength);
            }
        }

        @Override
        public byte readByte() {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (remainder() == 0) {
                throw new IndexOutOfBoundsException();
            }

            final var childOffset = byteOffset / childBufferSize;
            final var childByteOffset = byteOffset++ - childOffset * childBufferSize;

            return children.get(childOffset).getByte(childByteOffset);
        }

        @Override
        public void readBytes(final byte[] target, int targetOffset, int length) {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (targetOffset < 0 || length < 0 || length > target.length - targetOffset || length > remainder()) {
                throw new IndexOutOfBoundsException();
            }

            var childOffset = byteOffset / childBufferSize;
            final var childLast = (byteOffset + length) / childBufferSize;

            final var childByteOffset = byteOffset - childOffset * childBufferSize;
            var childByteLength = Math.min(childBufferSize - childByteOffset, length);

            children.get(childOffset).getBytes(childByteOffset, target, targetOffset, childByteLength);
            byteOffset += childByteLength;

            while (++childOffset <= childLast) {
                targetOffset += childByteLength;
                length -= childByteLength;
                childByteLength = Math.min(childBufferSize, length);

                children.get(childOffset).getBytes(0, target, targetOffset, childByteLength);
                byteOffset += childByteLength;
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
}
