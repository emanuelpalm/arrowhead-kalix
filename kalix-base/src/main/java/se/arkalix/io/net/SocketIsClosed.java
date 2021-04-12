package se.arkalix.io.net;

import se.arkalix.io.IoException;

public class SocketIsClosed extends IoException {
    public SocketIsClosed(final Throwable cause) {
        super(cause);
    }
}
