package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.BufferNio;
import se.arkalix.io.buf._internal.BufferHeap;
import se.arkalix.io.buf._internal.DefaultBufferReader;
import se.arkalix.io.buf._internal.DefaultBufferWriter;

import java.nio.ByteBuffer;

public interface Buffer extends BufferReader, BufferWriter {
    static Buffer allocateDirect(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new BufferNio(ByteBuffer.allocateDirect(initialCapacity), maximumCapacity);
    }

    static Buffer allocateHeap(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new BufferHeap(new byte[initialCapacity], maximumCapacity);
    }

    static Buffer wrap(final ByteBuffer byteBuffer) {
        return new BufferNio(byteBuffer, byteBuffer.capacity());
    }

    static Buffer wrap(final byte[] byteArray) {
        return new BufferHeap(byteArray, byteArray.length);
    }

    void clear();

    default Buffer copy() {
        return copy(readOffset(), readableBytes());
    }

    Buffer copy(int offset, int length);

    default Buffer dupe() {
        return dupe(readOffset(), readableBytes());
    }

    Buffer dupe(int offset, int length);

    void offsets(int readOffset, int writeOffset);

    default BufferReader reader() {
        return new DefaultBufferReader(this);
    }

    default BufferWriter writer() {
        return new DefaultBufferWriter(this);
    }
}
