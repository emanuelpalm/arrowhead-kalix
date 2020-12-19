package se.arkalix.io.buf0;

import se.arkalix.io.IoException;

public class BufferException extends IoException {
    public BufferException() {
        super();
    }

    public BufferException(final String message) {
        super(message);
    }

    public BufferException(final Throwable cause) {
        super(cause);
    }
}
