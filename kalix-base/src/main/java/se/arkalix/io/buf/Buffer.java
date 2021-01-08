package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.*;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public interface Buffer extends BufferReader, BufferWriter {
    static Buffer allocate(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0 || initialCapacity > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        return new HeapBuffer(new byte[initialCapacity], maximumCapacity);
    }

    static Buffer allocateDirect(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0 || initialCapacity > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        return new NioBuffer(ByteBuffer.allocateDirect(initialCapacity), maximumCapacity);
    }

    static Buffer empty() {
        return EmptyBuffer.instance();
    }

    static Buffer wrap(final ByteBuffer byteBuffer) {
        return new NioBuffer(byteBuffer, byteBuffer.capacity());
    }

    static Buffer wrap(final byte[] byteArray) {
        return new HeapBuffer(byteArray, byteArray.length);
    }

    void clear();

    default Buffer copy() {
        return copy(readOffset(), readableBytes());
    }

    Buffer copy(int offset, int length);

    Buffer dupe();

    void offsets(int readOffset, int writeOffset);

    default BufferReader reader() {
        return new DefaultBufferReader(this);
    }

    default BufferWriter writer() {
        return new DefaultBufferWriter(this);
    }
}
