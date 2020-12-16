package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.ByteArrayBuffer;

public interface Buffer extends AutoCloseable {
    static Buffer wrap(final byte[] bytes) {
        return wrap(bytes, 0, bytes.length);
    }

    static Buffer wrap(final byte[] bytes, final int offset, final int length) {
        return ByteArrayBuffer.of(bytes, offset, length);
    }

    @Override
    void close();

    BufferReader closeAndRead();

    BufferWriter closeAndWrite();

    Buffer copy();
}
