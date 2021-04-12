package se.arkalix.io.fs;

import se.arkalix.io.IoException;

public class FileException extends IoException {
    public FileException() {
        super();
    }

    public FileException(final String message) {
        super(message);
    }

    public FileException(final Throwable cause) {
        super(cause);
    }

    public FileException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected FileException(
        final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
