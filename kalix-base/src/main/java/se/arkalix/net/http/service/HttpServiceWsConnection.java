package se.arkalix.net.http.service;

import se.arkalix.net.http.HttpConnectionWithArSystem;
import se.arkalix.util.concurrent.Future;

/**
 * A WebSocket connection established between a local service provider and a
 * remote service consumer.
 */
public interface HttpServiceWsConnection extends HttpConnectionWithArSystem {
    /**
     * Sends message to Arrowhead system at the other end of this connection.
     *
     * @param message Message to send.
     * @return Future completed when the message has been submitted or it is
     * known to have failed to be submitted.
     */
    Future<?> send(final HttpWsOutgoingMessage message);
}
