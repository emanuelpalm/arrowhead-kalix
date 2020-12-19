package se.arkalix.io.buf;

public interface Buffer extends BufferReader, BufferWriter {
    void offsets(int readOffset, int writeOffset);

    default void clear() {
        offsets(0, 0);
    }
}
