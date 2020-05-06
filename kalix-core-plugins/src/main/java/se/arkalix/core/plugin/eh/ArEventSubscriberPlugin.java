package se.arkalix.core.plugin.eh;

import se.arkalix.description.ProviderDescription;
import se.arkalix.plugin.Plugin;
import se.arkalix.util.concurrent.Future;

import java.util.Collection;
import java.util.Map;

/**
 * Event subscriber plugin.
 * <p>
 * Implementations of this plugin helps manage subscriptions for events using
 * the {@link ArEventSubscribe} and {@link ArEventUnsubscribe} services.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ArEventSubscriberPlugin extends Plugin {
    /**
     * Registers new event subscription.
     *
     * @param topic   Topic, or "eventType", that must be matched by
     *                received events. Topics are case insensitive and can
     *                only be subscribed to once.
     * @param handler Handler to receive matching events.
     * @return Future completed when subscription is registered.
     */
    default Future<ArEventSubscriptionHandle> subscribe(final String topic, final ArEventSubscriptionHandler handler) {
        return subscribe(topic, null, null, handler);
    }

    /**
     * Registers new event subscription.
     *
     * @param topic    Topic, or "eventType", that must be matched by
     *                 received events. Topics are case insensitive and can
     *                 only be subscribed to once.
     * @param metadata Metadata pairs that must be matched by received
     *                 events.
     * @param handler  Handler to receive matching events.
     * @return Future completed when subscription is registered.
     */
    default Future<ArEventSubscriptionHandle> subscribe(
        final String topic,
        final Map<String, String> metadata,
        final ArEventSubscriptionHandler handler)
    {
        return subscribe(topic, metadata, null, handler);
    }

    /**
     * Registers new event subscription.
     *
     * @param topic     Topic, or "eventType", that must be matched by
     *                  received events. Topics are case insensitive and
     *                  can only be subscribed to once.
     * @param providers Event providers to receive events from.
     * @param handler   Handler to receive matching events.
     * @return Future completed when subscription is registered.
     */
    default Future<ArEventSubscriptionHandle> subscribe(
        final String topic,
        final Collection<ProviderDescription> providers,
        final ArEventSubscriptionHandler handler)
    {
        return subscribe(topic, null, providers, handler);
    }

    /**
     * Registers new event subscription.
     *
     * @param topic     Topic, or "eventType", that must be matched by
     *                  received events. Topics are case insensitive and
     *                  can only be subscribed to once.
     * @param metadata  Metadata pairs that must be matched by received
     *                  events.
     * @param providers Event providers to receive events from.
     * @param handler   Handler to receive matching events.
     * @return Future completed when subscription is registered.
     */
    default Future<ArEventSubscriptionHandle> subscribe(
        final String topic,
        final Map<String, String> metadata,
        final Collection<ProviderDescription> providers,
        final ArEventSubscriptionHandler handler)
    {
        return subscribe(new ArEventSubscription()
            .topic(topic)
            .metadata(metadata)
            .providers(providers)
            .handler(handler));
    }

    /**
     * Registers new event subscription.
     *
     * @param subscription Subscription to register.
     * @return This builder.
     */
    Future<ArEventSubscriptionHandle> subscribe(final ArEventSubscription subscription);
}