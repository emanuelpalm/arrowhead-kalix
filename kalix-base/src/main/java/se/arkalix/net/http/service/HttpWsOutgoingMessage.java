package se.arkalix.net.http.service;

import se.arkalix.internal.net.DefaultMessageOutgoing;
import se.arkalix.net.MessageOutgoingWithImplicitEncoding;

/**
 * An outgoing WebSocket message.
 */
public class HttpWsOutgoingMessage
    extends DefaultMessageOutgoing<HttpWsOutgoingMessage>
    implements MessageOutgoingWithImplicitEncoding<HttpWsOutgoingMessage>
{
    @Override
    protected HttpWsOutgoingMessage self() {
        return this;
    }
}
