package se.arkalix.io.buf0;

import se.arkalix.io.buf0._internal.NioBuffer;

public interface Buffer extends BufferInput, BufferOutput {
    static Buffer wrap(final byte[] byteArray) {
        return NioBuffer.wrap(byteArray, 0, byteArray.length);
    }

    static Buffer wrap(final byte[] byteArray, final int offset, final int length) {
        return NioBuffer.wrap(byteArray, offset, length);
    }
}
