package se.arkalix.util.concurrent._internal;

import se.arkalix.util.Result;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Internal
public class FlowPublishers {
    private FlowPublishers() {}

    public static <T> Future<?> consume(final Flow.Publisher<T> publisher, final Consumer<T> consumer) {
        final var subscription = new AtomicReference<Flow.Subscription>(null);

        final var future = new FutureCompletion<>();
        future.setCancelCallback(() -> {
            final var subscription0 = subscription.getAndSet(null);
            if (subscription0 != null) {
                subscription0.cancel();
            }
        });

        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(final Flow.Subscription subscription0) {
                subscription0.request(Long.MAX_VALUE);
                subscription.set(subscription0);
            }

            @Override
            public void onNext(final T item) {
                try {
                    consumer.accept(item);
                }
                catch (final Throwable throwable) {
                    onError(throwable);
                }
            }

            @Override
            public void onError(final Throwable throwable) {
                if (!future.isCompleted()) {
                    future.complete(Result.ofFault(throwable));
                }
                final var subscription0 = subscription.getAndSet(null);
                if (subscription0 != null) {
                    subscription0.cancel();
                }
            }

            @Override
            public void onComplete() {
                if (!future.isCompleted()) {
                    future.complete(Result.done());
                }
                subscription.set(null);
            }
        });

        return future;
    }
}
