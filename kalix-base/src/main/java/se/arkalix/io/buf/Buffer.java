package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.BufferSlice;

public interface Buffer extends BufferReader, BufferWriter {
    void clear();

    default Buffer copy() {
        return copy(readOffset(), readableBytes());
    }

    Buffer copy(int offset, int length);

    Buffer dupe();

    void offsets(int readOffset, int writeOffset);

    default Buffer slice() {
        return slice(readOffset(), readableBytes());
    }

    default Buffer slice(int offset, int length) {
        return BufferSlice.of(this, offset, length);
    }

    default BufferReader reader() {
        throw new UnsupportedOperationException("Not implemented");
    }

    default BufferWriter writer() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
