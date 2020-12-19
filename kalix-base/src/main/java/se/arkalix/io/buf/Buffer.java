package se.arkalix.io.buf;

public interface Buffer extends BufferReader, BufferWriter {
    default Buffer copy() {
        return copy(readOffset(), readableBytes());
    }

    Buffer copy(int offset, int length);

    Buffer dupe();

    default Buffer slice() {
        return slice(readOffset(), readableBytes());
    }

    Buffer slice(int offset, int length);

    void offsets(int readOffset, int writeOffset);

    default void clear() {
        offsets(0, 0);
    }

    BufferReader reader();

    BufferWriter writer();
}
