package se.arkalix.net.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A WebSocket handler, meant to process WebSocket events and messages.
 */
@FunctionalInterface
public interface HttpServiceWsHandler {
    Logger logger = LoggerFactory.getLogger(HttpServiceWsHandler.class);

    /**
     * Called to notify about a new WebSocket connection being established with
     * a remote host.
     *
     * @param connection Established WebSocket connection.
     */
    default void onOpen(final HttpServiceWsConnection connection) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} opened", connection);
        }
    }

    /**
     * Called to notify about a WebSocket connection being closed.
     *
     * @param connection Closed WebSocket connection.
     */
    default void onClose(final HttpServiceWsConnection connection) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} closed", connection);
        }
    }

    /**
     * Called to notify about an exception being thrown that prevents the
     * establishment or continued operation of a WebSocket.
     *
     * @param connection WebSocket subject of exception.
     * @param throwable  Exception causing this method to be called.
     */
    default void onFail(final HttpServiceWsConnection connection, final Throwable throwable) {
        if (logger.isWarnEnabled()) {
            logger.warn(connection + " error", throwable);
        }
    }

    /**
     * Called to notify about a message being received via an open WebSocket
     * connection associated with this handler.
     *
     * @param connection Connection through which message was received.
     * @param message    Received message.
     */
    void onMessage(HttpServiceWsConnection connection, HttpWsIncomingMessage message);
}
