package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.NioBuffer;
import se.arkalix.io.buf._internal.ByteArrayBuffer;
import se.arkalix.io.buf._internal.DefaultBufferReader;
import se.arkalix.io.buf._internal.DefaultBufferWriter;

import java.nio.ByteBuffer;

public interface Buffer extends BufferReader, BufferWriter {
    static Buffer allocateDirect(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new NioBuffer(ByteBuffer.allocateDirect(initialCapacity), maximumCapacity);
    }

    static Buffer allocateHeap(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new ByteArrayBuffer(new byte[initialCapacity], maximumCapacity);
    }

    static Buffer wrap(final ByteBuffer byteBuffer) {
        return new NioBuffer(byteBuffer, byteBuffer.capacity());
    }

    static Buffer wrap(final byte[] byteArray) {
        return new ByteArrayBuffer(byteArray, byteArray.length);
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
