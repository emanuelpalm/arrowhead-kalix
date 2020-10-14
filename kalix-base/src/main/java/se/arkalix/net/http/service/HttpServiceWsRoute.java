package se.arkalix.net.http.service;

import se.arkalix.net.http.HttpMethod;
import se.arkalix.util.InternalException;

import java.util.Objects;
import java.util.Optional;

/**
 * An {@link HttpRoute} specialized at establishing WebSocket connections.
 */
public class HttpServiceWsRoute extends HttpRoute implements HttpServiceWsHandler {
    private final String protocol;
    private final HttpServiceWsHandler handler;

    /**
     * Creates new {@link HttpService} WebSocket route.
     * <p>
     * Note that as per RFC 6455, WebSocket handshakes always use the GET
     * method.
     *
     * @param pattern  HTTP pattern to require for given request paths to match
     *                 this route. Use {@code null} to allow any path.
     * @param protocol WebSocket protocols to accept. Use {@code null} to
     *                 accept any protocol.
     * @param handler  The handler to execute with incoming messages and other
     *                 WebSocket events.
     */
    public HttpServiceWsRoute(final HttpPattern pattern, final String protocol, final HttpServiceWsHandler handler) {
        super(HttpMethod.GET, pattern, (request, response) -> {
            throw new InternalException("This HTTP route handler is not " +
                "meant to ever be called");
        });
        this.protocol = protocol;
        this.handler = Objects.requireNonNull(handler, "Expected handler");
    }

    /**
     * Gets WebSocket protocol, if any, that routed upgrade requests must match
     * for this route to be invoked.
     *
     * @return Route WebSocket protocol, if any.
     */
    public Optional<String> protocol() {
        return Optional.ofNullable(protocol);
    }

    @Override
    public void onOpen(final HttpServiceWsConnection connection) {
        handler.onOpen(connection);
    }

    @Override
    public void onClose(final HttpServiceWsConnection connection) {
        handler.onClose(connection);
    }

    @Override
    public void onFail(final HttpServiceWsConnection connection, final Throwable throwable) {
        handler.onFail(connection, throwable);
    }

    @Override
    public void onMessage(final HttpServiceWsConnection connection, final HttpWsIncomingMessage message) {
        handler.onMessage(connection, message);
    }
}
