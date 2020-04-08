package se.arkalix.core.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.arkalix.ArService;
import se.arkalix.core.plugin.dto.*;
import se.arkalix.description.ProviderDescription;
import se.arkalix.description.ServiceDescription;
import se.arkalix.descriptor.EncodingDescriptor;
import se.arkalix.descriptor.InterfaceDescriptor;
import se.arkalix.internal.security.identity.X509Keys;
import se.arkalix.net.http.HttpStatus;
import se.arkalix.net.http.client.HttpClient;
import se.arkalix.net.http.client.HttpClientResponseRejectedException;
import se.arkalix.plugin.Plug;
import se.arkalix.plugin.Plugin;
import se.arkalix.query.ServiceQuery;
import se.arkalix.security.access.AccessByToken;
import se.arkalix.security.identity.SystemIdentity;
import se.arkalix.util.Result;
import se.arkalix.util.concurrent.Future;
import se.arkalix.util.concurrent.FutureAnnouncement;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Objects;

import static se.arkalix.descriptor.SecurityDescriptor.CERTIFICATE;
import static se.arkalix.descriptor.SecurityDescriptor.NOT_SECURE;
import static se.arkalix.descriptor.TransportDescriptor.HTTP;
import static se.arkalix.dto.DtoEncoding.JSON;

/**
 * HTTP/JSON core integration plug-in.
 * <p>
 * This class helps one or more {@link se.arkalix.ArSystem systems} with
 * joining a local cloud by communicating with the mandatory Arrowhead core
 * services of that cloud using HTTP and JSON. More precisely, it (1) registers
 * and unregisters the {@link se.arkalix.ArSystem#provide(ArService) services
 * provided} by its systems, (2) retrieves the public key required to {@link
 * se.arkalix.security.access.AccessByToken validate consumer tokens}, as well
 * as (3) helps resolve {@link se.arkalix.ArSystem#consume() service
 * consumption queries}.
 */
public class HttpJsonCoreIntegrator implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(HttpJsonCoreIntegrator.class);

    private final String serviceRegistryBasePath;
    private final InetSocketAddress serviceRegistrySocketAddress;

    private final Object serviceDiscoveryLock = new Object();
    private FutureAnnouncement<HttpJsonServiceDiscovery> serviceDiscoveryAnnouncement = null;

    private final Object orchestrationLock = new Object();
    private FutureAnnouncement<HttpJsonOrchestration> orchestrationAnnouncement = null;

    private HttpClient client = null;

    private final Object authorizationKeyLock = new Object();
    private FutureAnnouncement<PublicKey> authorizationKeyAnnouncement = null;

    private HttpJsonCoreIntegrator(
        final InetSocketAddress serviceRegistrySocketAddress,
        final String serviceRegistryBasePath)
    {
        this.serviceRegistryBasePath = serviceRegistryBasePath;
        this.serviceRegistrySocketAddress = serviceRegistrySocketAddress;
    }

    /**
     * Creates new HTTP/JSON core service integrator that tries to enter a
     * local cloud using the service registry system at the specified {@code
     * socketAddress} as starting point.
     * <p>
     * The service URI, or base path, of the service registry system is assumed
     * to be {@code "/serviceregistry"}.
     *
     * @param socketAddress IP address or hostname and port of service registry
     *                      system to use for entering local cloud.
     * @return New core integrator.
     */
    public static HttpJsonCoreIntegrator enterViaServiceRegistry(final InetSocketAddress socketAddress) {
        return enterViaServiceRegistry(socketAddress, "/serviceregistry");
    }

    /**
     * Creates new HTTP/JSON core service integrator that tries to enter a
     * local cloud using the service registry system at the specified {@code
     * socketAddress} as starting point.
     * <p>
     * If the service URI, or base path, of the service registry system is not
     * known, please use {@link #enterViaServiceRegistry(InetSocketAddress)}.
     *
     * @param socketAddress IP address or hostname and port of service registry
     *                      system to use for entering local cloud.
     * @param basePath      The base path that HTTP request URIs must be
     *                      prefixed with in order for them to be received by
     *                      the service discovery service of the service
     *                      registry system available via {@code
     *                      socketAddress}.
     * @return New core integrator.
     */
    public static HttpJsonCoreIntegrator enterViaServiceRegistry(
        final InetSocketAddress socketAddress,
        final String basePath)
    {
        Objects.requireNonNull(socketAddress, "Expected socketAddress");
        return new HttpJsonCoreIntegrator(socketAddress, basePath);
    }

    @Override
    public void onAttach(final Plug plug) throws Exception {
        client = HttpClient.from(plug.system());

        if (logger.isInfoEnabled()) {
            logger.info("HTTP/JSON core integrator attached to \"{}\"", plug.system().name());
        }
    }

    @Override
    public void onDetach(final Plug plug) {
        if (logger.isInfoEnabled()) {
            logger.info("HTTP/JSON core integrator detached from \"{}\"", plug.system().name());
        }
    }

    @Override
    public void onDetach(final Plug plug, final Throwable cause) {
        if (logger.isErrorEnabled()) {
            logger.error("HTTP/JSON core integrator forcibly detached " +
                "from \"" + plug.system().name() + "\"", cause);
        }
    }

    @Override
    public Future<?> onServicePrepared(final Plug plug, final ArService service) {
        final var accessPolicy = service.accessPolicy();
        if (accessPolicy instanceof AccessByToken) {
            return requestAuthorizationKey().map(authorizationKey -> {
                ((AccessByToken) accessPolicy).authorizationKey(authorizationKey);
                return null;
            });
        }
        return Future.done();
    }

    @Override
    public Future<?> onServiceProvided(final Plug plug, final ServiceDescription service) {
        if (logger.isInfoEnabled()) {
            logger.info("System \"{}\" is now registering \"{}\" ...", plug.system().name(), service.name());
        }
        final var provider = service.provider();
        final var providerSocketAddress = provider.socketAddress();
        final var registration = new ServiceRegistrationBuilder()
            .name(service.name())
            .provider(new SystemDetailsBuilder()
                .name(provider.name())
                .hostname(providerSocketAddress.getHostString())
                .port(providerSocketAddress.getPort())
                .publicKeyBase64(provider.isSecure()
                    ? Base64.getEncoder().encodeToString(provider.publicKey().getEncoded())
                    : null)
                .build())
            .uri(service.uri())
            .security(service.security())
            .metadata(service.metadata())
            .version(service.version())
            .interfaces(new ArrayList<>(service.interfaces()))
            .build();

        return requestServiceDiscovery().flatMap(serviceDiscovery -> serviceDiscovery
            .register(registration)
            .flatMapCatch(HttpClientResponseRejectedException.class, fault -> {
                if (fault.status() == HttpStatus.BAD_REQUEST) {
                    return fault.unwrap()
                        .bodyAs(JSON, ErrorDto.class)
                        .flatMap(error -> {
                            if (!"INVALID_PARAMETER".equals(error.type())) {
                                return Future.failure(error.toException());
                            }
                            return serviceDiscovery.unregister(
                                service.name(),
                                provider.name(),
                                providerSocketAddress.getHostString(),
                                providerSocketAddress.getPort())
                                .flatMap(ignored -> serviceDiscovery.register(registration).pass(null));
                        })
                        .pass(null);
                }
                return Future.failure(fault);
            })
            .mapResult(result -> {
                if (result.isSuccess()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("System \"{}\" has registered \"{}\"",
                            plug.system().name(), service.name());
                    }
                }
                else {
                    if (logger.isErrorEnabled()) {
                        logger.error("System \"" + plug.system().name() +
                            "\" failed to register \"" + service.name() +
                            "\"", result.fault());
                    }
                }
                return result;
            }));
    }

    @Override
    public void onServiceDismissed(final Plug plug, final ServiceDescription service) {
        if (logger.isInfoEnabled()) {
            logger.info("System \"{}\" is now unregistering \"{}\" ...", plug.system().name(), service.name());
        }
        final var provider = service.provider();
        final var providerSocketAddress = provider.socketAddress();
        requestServiceDiscovery()
            .flatMap(serviceDiscovery -> serviceDiscovery.unregister(
                service.name(),
                provider.name(),
                providerSocketAddress.getHostString(),
                providerSocketAddress.getPort()))
            .onResult(result -> {
                if (result.isSuccess()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("System \"{}\" has unregistered \"{}\"",
                            plug.system().name(), service.name());
                    }
                }
                else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Failed to unregister service \"" +
                            service.name() + "\"", result.fault());
                    }
                }
            });
    }

    @Override
    public Future<Collection<ServiceDescription>> onServiceQueried(final Plug plug, final ServiceQuery query) {
        throw new UnsupportedOperationException(); // TODO_ Orchestrator
    }

    private Future<HttpJsonServiceDiscovery> requestServiceDiscovery() {
        synchronized (serviceDiscoveryLock) {
            if (serviceDiscoveryAnnouncement == null) {
                if (logger.isInfoEnabled()) {
                    logger.info("HTTP/JSON core integrator connecting to " +
                        "\"service_registry\" system at {} ...", serviceRegistrySocketAddress);
                }
                serviceDiscoveryAnnouncement = client.connect(serviceRegistrySocketAddress)
                    .mapResult(result -> {
                        if (result.isFailure()) {
                            return Result.failure(result.fault());
                        }
                        final var connection = result.value();
                        final var isSecure = connection.isSecure();
                        final ProviderDescription provider;
                        if (isSecure) {
                            final var identity = new SystemIdentity(connection.certificateChain());
                            final var name = identity.name();
                            if (!Objects.equals(name, "service_registry")) {
                                return Result.failure(new ArCoreIntegrationException("" +
                                    "HTTP/JSON core integrator connected to " +
                                    "system at " + serviceRegistrySocketAddress +
                                    " and found that its certificate name " +
                                    "is \"" + name + "\" while expecting it " +
                                    "to be \"service_registry\"; failed to " +
                                    "resolve service discovery service "));
                            }
                            provider = new ProviderDescription(name, serviceRegistrySocketAddress, identity.publicKey());
                        }
                        else {
                            provider = new ProviderDescription("service_registry", serviceRegistrySocketAddress);
                        }

                        final var serviceDiscovery = new HttpJsonServiceDiscovery(client,
                            new ServiceDescription.Builder()
                                .name("service-discovery")
                                .provider(provider)
                                .uri(serviceRegistryBasePath)
                                .security(isSecure ? CERTIFICATE : NOT_SECURE)
                                .interfaces(InterfaceDescriptor.getOrCreate(HTTP, isSecure, EncodingDescriptor.JSON))
                                .build());

                        connection.close();

                        if (logger.isInfoEnabled()) {
                            logger.info("HTTP/JSON core integrator " +
                                "connected to \"service_registry\" system " +
                                "at {}", serviceRegistrySocketAddress);
                        }

                        return Result.success(serviceDiscovery);
                    })
                    .ifFailure(Throwable.class, fault -> {
                        if (logger.isErrorEnabled()) {
                            logger.error("HTTP/JSON core integrator failed to " +
                                "connect to \"service_registry\" system at " +
                                serviceRegistrySocketAddress, fault);
                        }
                    })
                    .toAnnouncement();
            }
            return serviceDiscoveryAnnouncement.subscribe();
        }
    }

    private Future<PublicKey> requestAuthorizationKey() {
        synchronized (authorizationKeyLock) {
            if (authorizationKeyAnnouncement == null) {
                if (logger.isInfoEnabled()) {
                    logger.info("HTTP/JSON core integrator requesting authorization key ...");
                }
                authorizationKeyAnnouncement = requestServiceDiscovery()
                    .flatMap(serviceDiscovery -> serviceDiscovery.query(new ServiceQueryBuilder()
                        .name("auth-public-key")
                        .build()))
                    .mapResult(result -> {
                        if (result.isFailure()) {
                            return Result.failure(result.fault());
                        }
                        final var services = result.value().services();
                        if (services.size() == 0) {
                            return Result.failure(new ArCoreIntegrationException("" +
                                "No \"auth-public-key\" service seems to be " +
                                "available via the service registry at: " +
                                serviceRegistrySocketAddress + "; token " +
                                "authorization not possible"));
                        }

                        String publicKeyBase64 = null;
                        for (final var service : services) {
                            final var key = service.provider().publicKeyBase64();
                            if (key.isPresent()) {
                                publicKeyBase64 = key.get();
                                break;
                            }
                        }
                        if (publicKeyBase64 == null) {
                            return Result.failure(new ArCoreIntegrationException("" +
                                "Even though the service registry provided " +
                                "descriptions for " + services.size() + " " +
                                "\"auth-public-key\" service(s), none of them " +
                                "contains an authorization system public key; " +
                                "token authorization not possible"));
                        }

                        final PublicKey publicKey;
                        try {
                            publicKey = X509Keys.parsePublicKey(publicKeyBase64);
                        }
                        catch (final NoSuchAlgorithmException exception) {
                            return Result.failure(new ArCoreIntegrationException("" +
                                "The \"auth-public-key\" service provider public " +
                                "key seems to use an unsupported key algorithm; " +
                                "token authorization not possible", exception));
                        }

                        if (logger.isInfoEnabled()) {
                            logger.info("Authorization key retrieved: {}", publicKeyBase64);
                        }

                        return Result.success(publicKey);
                    })
                    .ifFailure(Throwable.class, fault -> {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Failed to retrieve authorization key", fault);
                        }
                    })
                    .toAnnouncement();
            }
            return authorizationKeyAnnouncement.subscribe();
        }
    }

    private Future<HttpJsonOrchestration> requestOrchestration() {
        synchronized (orchestrationLock) {
            if (orchestrationAnnouncement == null) {
                if (logger.isInfoEnabled()) {
                    logger.info("HTTP/JSON core integrator connecting to " +
                        "\"orchestrator\" system ...");
                }
                final var isSecure = client.isSecure();
                orchestrationAnnouncement = requestServiceDiscovery()
                    .flatMap(serviceDiscovery -> serviceDiscovery.query(new ServiceQueryBuilder()
                        .name("orchestration")
                        .interfaces(InterfaceDescriptor.getOrCreate(HTTP, isSecure, EncodingDescriptor.JSON))
                        .securityModes(isSecure ? CERTIFICATE : NOT_SECURE)
                        .build()))
                    .flatMapResult(result -> {
                        if (result.isFailure()) {
                            return Future.failure(result.fault());
                        }
                        final var queryResult = result.value();
                        final var services = queryResult.services();
                        if (services.isEmpty()) {
                            return Future.failure(new ArCoreIntegrationException("" +
                                "No orchestration service available; cannot " +
                                "request orchestration rules"));
                        }
                        final var orchestration = new HttpJsonOrchestration(client,
                            services.get(0).toServiceDescription());

                        if (logger.isInfoEnabled()) {
                            logger.info("Orchestration service resolved at {}",
                                orchestration.service().provider().socketAddress());
                        }

                        return Future.success(orchestration);
                    })
                    .ifFailure(Throwable.class, fault -> {
                        if (logger.isErrorEnabled()) {
                            logger.error("HTTP/JSON core integrator failed " +
                                "to connect to \"orchestrator\" system", fault);
                        }
                    })
                    .toAnnouncement();
            }
            return orchestrationAnnouncement.subscribe();
        }
    }
}