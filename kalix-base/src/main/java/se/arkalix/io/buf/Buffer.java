package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.NioBuffer;

public interface Buffer extends BufferInput, BufferOutput {
    static Buffer wrap(final byte[] byteArray) {
        return NioBuffer.wrap(byteArray, 0, byteArray.length);
    }

    static Buffer wrap(final byte[] byteArray, final int offset, final int length) {
        return NioBuffer.wrap(byteArray, offset, length);
    }
}
