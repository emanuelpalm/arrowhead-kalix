package se.arkalix.core.plugin.cp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.arkalix.ArSystem;
import se.arkalix.core.plugin.eh.ArEventSubscriberPluginFacade;
import se.arkalix.core.plugin.eh.EventSubscriptionHandle;
import se.arkalix.core.plugin.eh.HttpJsonEventSubscriberPlugin;
import se.arkalix.internal.util.concurrent.FutureCompletion;
import se.arkalix.plugin.Plugin;
import se.arkalix.plugin.PluginAttached;
import se.arkalix.plugin.PluginFacade;
import se.arkalix.util.Result;
import se.arkalix.util.concurrent.Future;
import se.arkalix.util.concurrent.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A HTTP/JSON {@link Plugin plugin} that helps manage the sending and
 * receiving of contract negotiation messages.
 * <p>
 * The plugin can be used as in following example:
 * <pre>
 *     // Provide plugin to created system.
 *     final var system = new ArSystem.Builder()
 *         .identity(identity)
 *         .trustStore(trustStore)
 *         .plugins(HttpJsonCloudPlugin.viaServiceRegistryAt(srSocketAddress),
 *             new HttpJsonContractNegotiationTrustedPlugin())
 *         .build();
 *
 *     // Collect the plugin's facade.
 *     final var negotiator = system.pluginFacadeOf(HttpJsonContractNegotiationTrustedPlugin.class)
 *         .map(facade -&gt; (ArContractNegotiationTrustedPluginFacade) facade)
 *         .orElseThrow(() -&gt; new IllegalStateException("Negotiator facade not available"));
 *
 *     // Send a negotiation offer to some relevant party and register response handlers.
 *     negotiator.offer(someOffer, new ArTrustedNegotiationHandler() {
 *         &#64;Override
 *         public void onAccept(final TrustedSessionCandidate candidate) {
 *             System.out.println("Accepted " + candidate);
 *         }
 *
 *         &#64;Override
 *         public void onOffer(final TrustedSessionCandidate candidate, final ArTrustedNegotiationResponder responder) {
 *             System.out.println("Received counter-offer " + candidate);
 *             System.out.println("Rejecting counter-offer ...");
 *             responder.reject()
 *                 .ifSuccess(ignored -&gt; System.out.println("Rejected counter-offer"))
 *                 .onFailure(Throwable::printStackTrace);
 *         }
 *
 *         &#64;Override
 *         public void onReject(final TrustedSessionCandidate candidate) {
 *             System.out.println("Rejected " + candidate);
 *         }
 *     });
 * </pre>
 * Use of this plugin requires that another plugin is available that performs
 * service resolution, such as the {@link
 * se.arkalix.core.plugin.HttpJsonCloudPlugin HttpJsonCloudPlugin}.
 */
@SuppressWarnings("unused")
public class HttpJsonTrustedContractNegotiatorPlugin implements ArTrustedContractNegotiatorPlugin {
    private static final Logger logger = LoggerFactory.getLogger(HttpJsonTrustedContractNegotiatorPlugin.class);

    @Override
    public Set<Class<? extends Plugin>> dependencies() {
        return Collections.singleton(HttpJsonEventSubscriberPlugin.class);
    }

    @Override
    public Future<PluginAttached> attachTo(
        final ArSystem system,
        final Map<Class<? extends Plugin>, PluginFacade> dependencies)
    {
        final var eventSubscriber = dependencies.get(HttpJsonEventSubscriberPlugin.class);
        if (eventSubscriber == null) {
            throw new IllegalStateException("Expected HttpJsonEventSubscriberPlugin to provide plugin facade");
        }
        final var attached = new Attached(system, (ArEventSubscriberPluginFacade) eventSubscriber);
        return attached.subscribe()
            .ifSuccess(ignored -> {
                if (logger.isInfoEnabled()) {
                    logger.info("HTTP/JSON contract negotiator plugin attached to \"{}\"", system.name());
                }
            })
            .ifFailure(Throwable.class, throwable -> {
                if (logger.isErrorEnabled()) {
                    logger.error("HTTP/JSON contract negotiator plugin " +
                        "failed to attached to \"" + system.name() + "\"", throwable);
                }
            })
            .pass(attached);
    }

    private static class Attached implements PluginAttached {
        private final Facade facade = new Facade();
        private final ArSystem system;
        private final ArEventSubscriberPluginFacade eventSubscriber;
        private final ExpectedEvents expectedEvents;

        private EventSubscriptionHandle eventSubscriptionHandle = null;

        private Attached(final ArSystem system, final ArEventSubscriberPluginFacade eventSubscriber) {
            this.system = Objects.requireNonNull(system, "Expected system");
            this.eventSubscriber = Objects.requireNonNull(eventSubscriber, "Expected eventSubscriber");

            expectedEvents = new ExpectedEvents(system);
        }

        public Future<?> subscribe() {
            return eventSubscriber
                .subscribe(ContractNegotiationConstants.TOPIC_UPDATE, (metadata, data) -> {
                    final long negotiationId;
                    try {
                        negotiationId = Long.parseLong(data);
                    }
                    catch (final Throwable throwable) {
                        logger.warn("HTTP/JSON contract negotiator received " +
                            "contract event with an invalid session " +
                            "identifier; cannot process event [data=" + data +
                            ", metadata=" + metadata + "]", throwable);
                        return;
                    }

                    final var offerorName = metadata.get("offeror");
                    if (offerorName == null) {
                        logger.warn("HTTP/JSON contract negotiator received " +
                            "contract event without a named offeror; " +
                            "cannot process event [data={}, metadata={}]", data, metadata);
                        return;
                    }

                    final var receiverName = metadata.get("receiver");
                    if (receiverName == null) {
                        logger.warn("HTTP/JSON contract negotiator received " +
                            "contract event without a named receiver; " +
                            "cannot process event [data={}, metadata={}]", data, metadata);
                        return;
                    }

                    final var status = metadata.get("status");
                    if (status == null) {
                        logger.warn("HTTP/JSON contract negotiator received " +
                            "contract event without a status; cannot " +
                            "process event [data={}, metadata={}]", data, metadata);
                        return;
                    }

                    if (!expectedEvents.tryToHandle(offerorName, receiverName, negotiationId, status)) {
                        logger.debug("HTTP/JSON contract negotiator " +
                            "received contract event that does name an " +
                            "expected offeror, receiver, negotiation " +
                            "identifier and/or action; event ignored " +
                            "[data={}, metadata={}]", data, metadata);
                    }
                })
                .ifSuccess(handle -> {
                    synchronized (this) {
                        eventSubscriptionHandle = handle;
                    }
                });
        }

        @Override
        public Optional<PluginFacade> facade() {
            return Optional.of(facade);
        }

        @Override
        public void onDetach() {
            synchronized (this) {
                eventSubscriptionHandle.unsubscribe();
                eventSubscriptionHandle = null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("HTTP/JSON contract negotiator plugin detached " +
                    "from \"{}\"", system.name());
            }
        }

        @Override
        public void onDetach(final Throwable cause) {
            if (logger.isErrorEnabled()) {
                logger.error("HTTP/JSON contract negotiator plugin forcibly " +
                    "detached from \"" + system.name() + "\"", cause);
            }
        }

        private class Facade implements ArTrustedContractNegotiatorPluginFacade {
            @Override
            public void listen(
                final String receiverName,
                final Supplier<TrustedContractNegotiatorHandler> handlerFactory)
            {
                expectedEvents.add(new ExpectedOfferForReceiver(system, receiverName, handlerFactory));
            }

            @Override
            public void offer(final TrustedContractOfferDto offer, final TrustedContractNegotiatorHandler handler) {
                system.consume()
                    .using(HttpJsonTrustedContractNegotiationService.factory())
                    .flatMap(service -> service.offer(offer))
                    .ifSuccess(negotiationId ->
                        expectedEvents.add(new ExpectedResponseToOffer(
                            system, handler,
                            offer.offerorName(), offer.receiverName(), negotiationId,
                            offer.expiresIn())))
                    .onFailure(handler::onFault);
            }
        }
    }

    private static class ExpectedEvents {
        private final ArSystem system;
        private final Queue<ExpectedEvent> expectedEvents = new ConcurrentLinkedQueue<>();

        private ExpectedEvents(final ArSystem system) {
            this.system = system;
        }

        public void add(final ExpectedEvent expectedEvent) {
            expectedEvents.add(expectedEvent);
        }

        public boolean tryToHandle(
            final String offerorName,
            final String receiverName,
            final long negotiationId,
            final String status)
        {
            final var it = expectedEvents.iterator();
            while (it.hasNext()) {
                final var expectedEvent = it.next();
                if (expectedEvent.matches(offerorName, receiverName, negotiationId, status)) {
                    if (expectedEvent.isToBeRemovedWhenMatched()) {
                        it.remove();
                    }
                    system.consume()
                        .using(HttpJsonTrustedContractObservationService.factory())
                        .flatMap(service -> service.getByNamesAndId(offerorName, receiverName, negotiationId)
                            .map(optionalNegotiation -> optionalNegotiation
                                .orElseThrow(() -> new IllegalStateException("" +
                                    "Advertised negotiation [offeror=" +
                                    offerorName + ", receiver=" + receiverName +
                                    ", id=" + negotiationId + ", status=" +
                                    status + "] not available via service \"" +
                                    service.service().name() + "\"; cannot " +
                                    "present negotiation update to " +
                                    "negotiation handler"))))
                        .flatMap(expectedEvent::handle)
                        .ifSuccess(optionalNewExpectedEvent -> optionalNewExpectedEvent.ifPresent(expectedEvents::add))
                        .onFailure(fault -> logger.error("Failed to handle " +
                            "negotiation [offeror=" + offerorName + ", " +
                            "receiver=" + receiverName + ", id=" +
                            negotiationId + ", status=" + status + "]", fault));
                    return true;
                }
            }
            return false;
        }
    }

    private interface ExpectedEvent {
        boolean matches(
            final String offerorName,
            final String receiverName,
            final long negotiationId,
            final String status);

        boolean isToBeRemovedWhenMatched();

        Future<Optional<ExpectedEvent>> handle(final TrustedContractNegotiationDto negotiation);
    }

    private static class ExpectedOfferForReceiver implements ExpectedEvent {
        private final ArSystem system;
        private final String receiverName;
        private final Supplier<TrustedContractNegotiatorHandler> handlerFactory;

        private ExpectedOfferForReceiver(
            final ArSystem system,
            final String receiverName,
            final Supplier<TrustedContractNegotiatorHandler> handlerFactory)
        {
            this.system = Objects.requireNonNull(system, "Expected system");
            this.receiverName = Objects.requireNonNull(receiverName, "Expected receiverName");
            this.handlerFactory = Objects.requireNonNull(handlerFactory, "Expected handlerFactory");
        }

        @Override
        public boolean matches(
            final String offerorName,
            final String receiverName,
            final long negotiationId,
            final String status)
        {
            return this.receiverName.equals(receiverName) && "OFFERING".equalsIgnoreCase(status);
        }

        @Override
        public boolean isToBeRemovedWhenMatched() {
            return false;
        }

        @Override
        public Future<Optional<ExpectedEvent>> handle(final TrustedContractNegotiationDto negotiation) {
            if (negotiation.status() != ContractNegotiationStatus.OFFERING) {
                throw new IllegalStateException("Expected handled " +
                    "negotiation to have status OFFERING; received " + negotiation);
            }
            if (!receiverName.equals(negotiation.offer().receiverName())) {
                throw new IllegalStateException("Expected handled " +
                    "negotiation to have receiver \"" + receiverName + "\"; " +
                    "received " + negotiation);
            }
            final var expiresIn = Duration.between(Instant.now(), negotiation.offer().validUntil());
            if (expiresIn.isNegative()) {
                throw new IllegalStateException("Handled negotiation has " +
                    "already expired; " + negotiation);
            }

            final var expectedResponseToOffer = new ExpectedResponseToOffer(system, handlerFactory.get(),
                negotiation.offer().offerorName(), receiverName, negotiation.id(), expiresIn);

            expectedResponseToOffer.handle(negotiation);

            return Future.success(Optional.of(expectedResponseToOffer));
        }
    }

    private static class ExpectedResponseToOffer implements ExpectedEvent {
        private final ArSystem system;
        private final TrustedContractNegotiatorHandler handler;
        private final String offerorName;
        private final String receiverName;
        private final long negotiationId;

        private final AtomicReference<Future<?>> expirationFuture;
        private final AtomicBoolean isExpired = new AtomicBoolean(false);

        private ExpectedResponseToOffer(
            final ArSystem system,
            final TrustedContractNegotiatorHandler handler,
            final String offerorName,
            final String receiverName,
            final long negotiationId,
            final Duration expiresIn)
        {
            this.system = Objects.requireNonNull(system, "Expected system");
            this.handler = Objects.requireNonNull(handler, "Expected handler");
            this.offerorName = Objects.requireNonNull(offerorName, "Expected offerorName");
            this.receiverName = Objects.requireNonNull(receiverName, "Expected receiverName");
            this.negotiationId = negotiationId;
            Objects.requireNonNull(expiresIn, "Expected expiresIn");

            expirationFuture = new AtomicReference<>(Schedulers.fixed().schedule(expiresIn, this::expire));
        }

        public void expire() {
            isExpired.set(true);
            try {
                handler.onExpiry();
            }
            catch (final Throwable throwable) {
                handler.onFault(throwable);
            }
        }

        public void refresh(final Duration expiresIn) {
            final var future = expirationFuture.getAndSet(Schedulers.fixed().schedule(expiresIn, this::expire));
            if (future != null) {
                future.cancel();
            }
        }

        @Override
        public boolean matches(
            final String offerorName,
            final String receiverName,
            final long negotiationId,
            final String status)
        {
            if (isExpired.get() || this.negotiationId != negotiationId) {
                return false;
            }
            switch (status.toUpperCase()) {
            case "OFFERING":
                return this.offerorName.equals(receiverName) && this.receiverName.equals(offerorName);

            case "ACCEPTED":
            case "REJECTED":
                return this.offerorName.equals(offerorName) && this.receiverName.equals(receiverName);

            default:
                return false;
            }
        }

        @Override
        public boolean isToBeRemovedWhenMatched() {
            return true;
        }

        @Override
        public Future<Optional<ExpectedEvent>> handle(final TrustedContractNegotiationDto negotiation) {
            try {
                switch (negotiation.status()) {
                case OFFERING:
                    final var future = new FutureCompletion<Optional<ExpectedEvent>>();
                    handler.onOffer(negotiation, new TrustedContractNegotiatorResponder() {
                        @Override
                        public Future<?> accept() {
                            return system.consume()
                                .using(HttpJsonTrustedContractNegotiationService.factory())
                                .flatMap(service -> service.accept(new TrustedContractAcceptanceBuilder()
                                    .negotiationId(negotiationId)
                                    .acceptedAt(Instant.now())
                                    .build()));
                        }

                        @Override
                        public Future<?> offer(final SimplifiedContractCounterOffer offer) {
                            final var counterOffer = new TrustedContractCounterOfferBuilder()
                                .negotiationId(negotiationId)
                                .offerorName(offerorName)
                                .receiverName(receiverName)
                                .validAfter(offer.validAfter())
                                .validUntil(offer.validUntil())
                                .contracts(offer.contracts())
                                .offeredAt(offer.offeredAt())
                                .build();
                            return system.consume()
                                .using(HttpJsonTrustedContractNegotiationService.factory())
                                .flatMap(service -> service.counterOffer(counterOffer))
                                .ifSuccess(ignored -> {
                                    ExpectedResponseToOffer.this.refresh(counterOffer.expiresIn());
                                    future.complete(Result.success(Optional.of(ExpectedResponseToOffer.this)));
                                })
                                .ifFailure(Throwable.class, ignored ->
                                    future.complete(Result.success(Optional.empty())));
                        }

                        @Override
                        public Future<?> reject() {
                            return system.consume()
                                .using(HttpJsonTrustedContractNegotiationService.factory())
                                .flatMap(service -> service.reject(new TrustedContractRejectionBuilder()
                                    .negotiationId(negotiationId)
                                    .rejectedAt(Instant.now())
                                    .build()));
                        }
                    });
                    return future;

                case ACCEPTED:
                    handler.onAccept(negotiation);
                    break;

                case REJECTED:
                    handler.onReject(negotiation);
                    break;
                }
            }
            catch (final Throwable throwable) {
                handler.onFault(throwable);
            }
            return Future.success(Optional.empty());
        }
    }
}
