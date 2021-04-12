package se.arkalix.io.net;

import se.arkalix.io.IoException;

public class ListenerIsClosed extends IoException {
    public ListenerIsClosed(final Throwable cause) {
        super(cause);
    }
}
