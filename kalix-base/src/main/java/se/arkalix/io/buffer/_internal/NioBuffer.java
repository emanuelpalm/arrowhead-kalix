package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferCapacityNotIncreased;
import se.arkalix.io.buffer.BufferIsClosed;
import se.arkalix.io.buffer.BufferView;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Internal
public class NioBuffer implements Buffer {
    private final Consumer<ByteBuffer> onClose;
    private final ByteBuffer inner;
    private final AtomicInteger viewCount = new AtomicInteger(0);

    private boolean isClosed = false;

    public NioBuffer(final Consumer<ByteBuffer> onClose, final ByteBuffer inner) {
        this.onClose = Objects.requireNonNull(onClose, "onClose");
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public int capacity() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return inner.limit() + 1;
    }

    @Override
    public void capacity(final int capacity) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (capacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (capacity > inner.capacity()) {
            throw new BufferCapacityNotIncreased();
        }
        inner.limit(Math.max(capacity - 1, 0));
    }

    @Override
    public void drop() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            onClose.accept(inner);
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
        return inner.position();
    }

    @Override
    public void offset(final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > inner.limit()) {
            throw new BufferCapacityNotIncreased();
        }
        inner.position(offset);
    }

    @Override
    public void putByte(final int offset, final byte b) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > inner.limit()) {
            throw new BufferCapacityNotIncreased();
        }
        inner.put(offset, b);
    }

    @Override
    public void putBytes(int offset, final byte[] source, int sourceOffset, int length) {
        Objects.requireNonNull(source, "source");
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0 || sourceOffset < 0 || length < 0 || length > source.length - sourceOffset) {
            throw new IndexOutOfBoundsException();
        }
        if (length > inner.remaining()) {
            throw new BufferCapacityNotIncreased();
        }
        while (--length >= 0) {
            inner.put(offset++, source[sourceOffset++]);
        }
    }

    @Override
    public BufferView view() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            inner.limit(inner.position());
            inner.position(0);
            return new View(viewCount, onClose, inner);
        }
        finally {
            isClosed = true;
        }
    }

    private static class View implements BufferView {
        private final Consumer<ByteBuffer> onClose;
        private final ByteBuffer inner;
        private final AtomicInteger viewCount;

        private boolean isClosed = false;

        private View(final AtomicInteger viewCount, final Consumer<ByteBuffer> onClose, final ByteBuffer inner) {
            this.viewCount = Objects.requireNonNull(viewCount, "viewCount");
            this.onClose = Objects.requireNonNull(onClose, "onClose");
            this.inner = Objects.requireNonNull(inner, "inner");

            viewCount.incrementAndGet();
        }

        @Override
        public BufferView dupe() {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            return new View(viewCount, onClose, inner.slice());
        }

        @Override
        public void close() {
            if (isClosed) {
                return;
            }
            try {
                if (viewCount.decrementAndGet() == 0) {
                    onClose.accept(inner);
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
            return inner.position();
        }

        @Override
        public void offset(final int offset) {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (offset < 0 || offset > inner.limit()) {
                throw new IndexOutOfBoundsException();
            }
            inner.position(offset);
        }

        @Override
        public byte getByte(final int offset) {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (offset < 0 || offset > inner.limit()) {
                throw new IndexOutOfBoundsException();
            }
            return inner.get(offset);
        }

        @Override
        public void getBytes(int offset, final byte[] target, int targetOffset, int length) {
            Objects.requireNonNull(target, "target");
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (offset < 0 || targetOffset < 0 || length < 0 ||
                length > target.length - targetOffset || length > inner.remaining())
            {
                throw new IndexOutOfBoundsException();
            }
            while (--length >= 0) {
                target[targetOffset++] = inner.get(offset++);
            }
        }

        @Override
        public int size() {
            return inner.limit() + 1;
        }
    }
}
