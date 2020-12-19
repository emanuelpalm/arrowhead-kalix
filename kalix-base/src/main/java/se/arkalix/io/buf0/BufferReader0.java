package se.arkalix.io.buf0;

import se.arkalix.io.buf0._internal.ByteArrayBufferReader0;
import se.arkalix.io.buf0._internal.EmptyBufferReader0;
import se.arkalix.io.mem.Reader;

public interface BufferReader0 extends AutoCloseable, Reader {
    static BufferReader0 empty() {
        return EmptyBufferReader0.instance();
    }

    static BufferReader0 wrap(final byte[] bytes) {
        return wrap(bytes, 0, bytes.length);
    }

    static BufferReader0 wrap(final byte[] bytes, final int offset, final int length) {
        return ByteArrayBufferReader0.of(bytes, offset, length);
    }

    @Override
    void close();

    Buffer0 copy();

    BufferReader0 dupe();

    default int remainder() {
        return limit() - offset();
    }
}
