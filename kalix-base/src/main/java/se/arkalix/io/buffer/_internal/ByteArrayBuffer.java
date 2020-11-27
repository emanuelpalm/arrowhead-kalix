package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferCapacityNotIncreased;
import se.arkalix.io.buffer.BufferIsClosed;
import se.arkalix.io.buffer.BufferReader;
import se.arkalix.util.annotation.Internal;

import java.util.Objects;

@Internal
public class ByteArrayBuffer implements Buffer {
    private final byte[] byteArray;
    private final int givenLength;
    private final int givenOffset;

    private boolean isClosed = false;
    private int capacity;
    private int offset = 0; // Relative to `givenOffset`.

    public ByteArrayBuffer(final byte[] byteArray, final int offset, final int length) {
        if (offset < 0 || length < 0 || offset + length > byteArray.length) {
            throw new IndexOutOfBoundsException();
        }
        this.byteArray = Objects.requireNonNull(byteArray, "byteArray");
        givenOffset = offset;
        givenLength = capacity = length;
    }

    @Override
    public int capacity() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return capacity;
    }

    @Override
    public void capacity(final int capacity) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (capacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (capacity > givenLength) {
            throw new BufferCapacityNotIncreased();
        }
        this.capacity = capacity;
    }

    @Override
    public void close() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        isClosed = true;
    }

    @Override
    public int offset() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        return offset;
    }

    @Override
    public void offset(final int offset) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset >= capacity) {
            throw new BufferCapacityNotIncreased();
        }
        this.offset = offset;
    }

    @Override
    public void putByte(final int offset, final byte b) {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset >= capacity) {
            throw new BufferCapacityNotIncreased();
        }
        byteArray[givenOffset + offset] = b;
    }

    @Override
    public void putBytes(final int offset, final byte[] source, final int sourceOffset, final int length) {
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
        System.arraycopy(source, sourceOffset, byteArray, givenOffset + offset, length);
    }

    @Override
    public BufferReader read() {
        if (isClosed) {
            throw new BufferIsClosed();
        }
        try {
            return new Reader(byteArray, givenOffset + offset, capacity);
        }
        finally {
            isClosed = true;
        }
    }

    public static class Reader implements BufferReader {
        private final byte[] byteArray;
        private final int length;

        private boolean isClosed = false;
        private int offset;

        public Reader(final byte[] byteArray, final int offset, final int length) {
            if (offset < 0 || length < 0 || offset + length > byteArray.length) {
                throw new IndexOutOfBoundsException();
            }
            this.byteArray = Objects.requireNonNull(byteArray, "byteArray");
            this.offset = offset;
            this.length = length;
        }

        @Override
        public void close() {
            isClosed = true;
        }

        @Override
        public BufferReader dupe() {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            return new Reader(byteArray, offset, length);
        }

        @Override
        public int offset() {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            return offset;
        }

        @Override
        public void offset(final int offset) {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (offset < 0 || offset > byteArray.length) {
                throw new IndexOutOfBoundsException();
            }
            this.offset = offset;
        }

        @Override
        public byte getByte(final int offset) {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (offset < 0 || offset > byteArray.length) {
                throw new IndexOutOfBoundsException();
            }
            return byteArray[offset];
        }

        @Override
        public void getBytes(final int offset, final byte[] target, final int targetOffset, final int length) {
            Objects.requireNonNull(target, "target");
            if (isClosed) {
                throw new BufferIsClosed();
            }
            if (offset < 0 || targetOffset < 0 || length < 0 ||
                length > target.length - targetOffset || length > remainder())
            {
                throw new IndexOutOfBoundsException();
            }
            System.arraycopy(byteArray, offset, target, targetOffset, length);
        }

        @Override
        public int size() {
            if (isClosed) {
                throw new BufferIsClosed();
            }
            return length;
        }
    }
}
