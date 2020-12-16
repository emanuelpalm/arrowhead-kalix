package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.ByteArrayBufferReader;
import se.arkalix.io.buf._internal.EmptyBufferReader;
import se.arkalix.io.mem.Reader;

public interface BufferReader extends AutoCloseable, Reader {
    static BufferReader empty() {
        return EmptyBufferReader.instance();
    }

    static BufferReader wrap(final byte[] bytes) {
        return wrap(bytes, 0, bytes.length);
    }

    static BufferReader wrap(final byte[] bytes, final int offset, final int length) {
        return ByteArrayBufferReader.of(bytes, offset, length);
    }

    @Override
    void close();

    Buffer copy();

    BufferReader dupe();

    default int remainder() {
        return limit() - offset();
    }
}
