package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.ByteArrayBuffer0;

public interface Buffer0 extends AutoCloseable {
    static Buffer0 wrap(final byte[] bytes) {
        return wrap(bytes, 0, bytes.length);
    }

    static Buffer0 wrap(final byte[] bytes, final int offset, final int length) {
        return ByteArrayBuffer0.of(bytes, offset, length);
    }

    @Override
    void close();

    BufferReader0 closeAndRead();

    BufferWriter0 closeAndWrite();

    Buffer0 copy();
}
