package se.arkalix.net.http.consumer._internal;

import se.arkalix.ArSystem;
import se.arkalix.ServiceRecord;
import se.arkalix.net.Encoding;
import se.arkalix.security.access.AccessType;
import se.arkalix.net.http.client.HttpClient;
import se.arkalix.net.http.consumer.HttpConsumer;
import se.arkalix.net.http.consumer.HttpConsumerConnection;
import se.arkalix.net.http.consumer.HttpConsumerConnectionException;
import se.arkalix.security.identity.SystemIdentity;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static se.arkalix.net.Transport.HTTP;

@Internal
public class DefaultHttpConsumer implements HttpConsumer {
    private final ArSystem system;
    private final HttpClient client;
    private final ServiceRecord service;
    private final Encoding encoding;
    private final String authorization;

    public DefaultHttpConsumer(
        final ArSystem system,
        final ServiceRecord service,
        final Collection<Encoding> encodings
    ) {
        this.system = Objects.requireNonNull(system, "system");
        this.service = Objects.requireNonNull(service, "service");

        client = HttpClient.from(system);

        final var isSecure = service.security() != AccessType.NOT_SECURE;
        if (isSecure != system.isSecure()) {
            if (isSecure) {
                throw new IllegalStateException("The provided system is " +
                    "configured to run in insecure mode, while the provided " +
                    "service \"" + service.name() + "\" is not; cannot " +
                    "consume service");
            }
            else {
                throw new IllegalStateException("The provided system is " +
                    "configured to run in secure mode, while the provided " +
                    "service \"" + service.name() + "\" is not; cannot " +
                    "consume service");
            }
        }

        final var compatibleInterfaceEntry = service.interfaceTokens()
            .entrySet()
            .stream()
            .filter(entry -> {
                final var descriptor = entry.getKey();
                return descriptor.transport() == HTTP &&
                    descriptor.isSecure() == isSecure &&
                    encodings.contains(descriptor.encoding());
            })
            .sorted((a, b) -> b.getValue().length() - a.getValue().length())
            .findAny()
            .orElseThrow(() -> new IllegalStateException("The service \"" +
                service.name() + "\" does not support any " +
                (isSecure ? "secure" : "insecure") + " HTTP interface with " +
                "any of the encodings " + encodings.stream()
                .map(Encoding::name)
                .collect(Collectors.joining(", ", "[", "]")) +
                "; cannot consume service"));

        encoding = compatibleInterfaceEntry.getKey().encoding();

        final var token = compatibleInterfaceEntry.getValue();
        authorization = token != null && token.length() > 0
            ? "Bearer " + token
            : null;
    }

    @Override
    public ServiceRecord service() {
        return service;
    }

    @Override
    public boolean isSecure() {
        return client.isSecure();
    }

    @Override
    public Future<HttpConsumerConnection> connect(final InetSocketAddress localSocketAddress) {
        return client.connect(service.provider().socketAddress(), localSocketAddress)
            .flatMap(connection -> {
                final SystemIdentity identity;
                if (isSecure()) {
                    identity = new SystemIdentity(connection.remoteCertificateChain());
                    if (!Objects.equals(identity.publicKey(), service.provider().publicKey())) {
                        return connection.close()
                            .fail(new HttpConsumerConnectionException("" +
                                "The public key known to be associated with the " +
                                "the consumed system \"" + service.provider().name() +
                                "\" does not match the public key in the " +
                                "certificate retrieved when connecting to it; " +
                                "cannot connect to service"));
                    }
                }
                else {
                    identity = null;
                }
                return Future.success(new DefaultHttpConsumerConnection(system, encoding, authorization, identity, connection));
            });
    }
}
