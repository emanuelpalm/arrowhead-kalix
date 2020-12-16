package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.ByteArrayBufferWriter;
import se.arkalix.io.mem.Writer;

public interface BufferWriter extends AutoCloseable, Writer {
    static BufferWriter wrap(final byte[] bytes) {
        return wrap(bytes, 0, bytes.length);
    }

    static BufferWriter wrap(final byte[] bytes, final int offset, final int length) {
        return ByteArrayBufferWriter.of(bytes, offset, length);
    }

    @Override
    void close();

    BufferReader closeAndRead();

    Buffer copy();

    default int space() {
        return limit() - offset();
    }
}
