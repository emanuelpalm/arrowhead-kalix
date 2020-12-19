package se.arkalix.io.buf0;

import se.arkalix.io.buf0._internal.ByteArrayBufferWriter0;
import se.arkalix.io.mem.Writer;

public interface BufferWriter0 extends AutoCloseable, Writer {
    static BufferWriter0 wrap(final byte[] bytes) {
        return wrap(bytes, 0, bytes.length);
    }

    static BufferWriter0 wrap(final byte[] bytes, final int offset, final int length) {
        return ByteArrayBufferWriter0.of(bytes, offset, length);
    }

    @Override
    void close();

    BufferReader0 closeAndRead();

    Buffer0 copy();

    default int space() {
        return limit() - offset();
    }
}
