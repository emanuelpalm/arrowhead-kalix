package se.arkalix.util.concurrent;

import se.arkalix.util.Result;
import se.arkalix.util.concurrent._internal.FutureConsumption;
import se.arkalix.util.concurrent._internal.FutureConsumptionWithExtraCancelTarget;
import se.arkalix.util.function.ThrowingConsumer;
import se.arkalix.util.function.ThrowingFunction;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@code Future} that always fails with a predetermined error.
 *
 * @param <V> Type of value that would have been included if successful.
 */
class FutureFault<V> implements Future<V> {
    private final Throwable fault;

    /**
     * Creates new failing {@link Future}.
     *
     * @param fault Throwable to include in {@code Future}.
     * @throws NullPointerException If {@code fault} is {@code null}.
     */
    public FutureFault(final Throwable fault) {
        this.fault = Objects.requireNonNull(fault);
    }

    @Override
    public void onResult(final Consumer<Result<V>> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        consumer.accept(Result.ofFault(fault));
    }

    @Override
    public void cancel() {
        // Does nothing.
    }

    @Override
    public void onFault(final Consumer<Throwable> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        consumer.accept(fault);
    }

    @Override
    public Future<V> ifValue(final ThrowingConsumer<V> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        return this;
    }

    @Override
    public Future<V> ifFault(final ThrowingConsumer<Throwable> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        try {
            consumer.accept(fault);
        }
        catch (final Throwable fault0) {
            fault0.addSuppressed(fault);
            return Future.fault(fault0);
        }
        return this;
    }

    @Override
    public <T extends Throwable> Future<V> ifFault(final Class<T> class_, final ThrowingConsumer<T> consumer) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        try {
            if (class_.isAssignableFrom(fault.getClass())) {
                consumer.accept(class_.cast(fault));
            }
        }
        catch (final Throwable fault0) {
            fault0.addSuppressed(fault);
            return Future.fault(fault0);
        }
        return this;
    }

    @Override
    public Future<V> always(final ThrowingConsumer<Result<V>> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        try {
            consumer.accept(Result.ofFault(fault));
        }
        catch (final Throwable fault0) {
            fault0.addSuppressed(fault);
            return Future.fault(fault0);
        }
        return this;
    }

    @Override
    public <U> Future<U> map(final ThrowingFunction<? super V, U> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return Future.fault(fault);
    }

    @Override
    public Future<V> mapCatch(final ThrowingFunction<Throwable, ? extends V> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault0 = fault;
        try {
            return Future.value(mapper.apply(fault0));
        }
        catch (final Throwable fault1) {
            fault1.addSuppressed(fault0);
            fault0 = fault1;
        }
        return Future.fault(fault0);
    }

    @Override
    public <U extends Throwable> Future<V> mapCatch(
        final Class<U> class_,
        final ThrowingFunction<U, ? extends V> mapper
    ) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault0 = fault;
        if (class_.isAssignableFrom(fault0.getClass())) {
            try {
                return Future.value(mapper.apply(class_.cast(fault0)));
            }
            catch (final Throwable fault1) {
                fault1.addSuppressed(fault0);
                fault0 = fault1;
            }
        }
        return Future.fault(fault0);
    }

    @Override
    public Future<V> mapFault(final ThrowingFunction<Throwable, Throwable> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault0 = fault;
        try {
            fault0 = mapper.apply(fault0);
        }
        catch (final Throwable fault1) {
            fault1.addSuppressed(fault0);
            fault0 = fault1;
        }
        return Future.fault(fault0);
    }

    @Override
    public <T extends Throwable> Future<V> mapFault(
        final Class<T> class_,
        final ThrowingFunction<T, Throwable> mapper
    ) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault0 = fault;
        if (class_.isAssignableFrom(fault0.getClass())) {
            try {
                fault0 = mapper.apply(class_.cast(fault0));
            }
            catch (final Throwable fault1) {
                fault1.addSuppressed(fault0);
                fault0 = fault1;
            }
        }
        return Future.fault(fault0);
    }

    @Override
    public <U> Future<U> mapResult(final ThrowingFunction<Result<V>, Result<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        try {
            return new FutureResult<>(mapper.apply(Result.ofFault(fault)));
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
    }

    @Override
    public Future<V> mapThrow(final ThrowingFunction<? super V, Throwable> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return Future.fault(fault);
    }

    @Override
    public <U> Future<U> flatMap(final ThrowingFunction<? super V, ? extends Future<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return Future.fault(fault);
    }

    @Override
    public Future<V> flatMapCatch(final ThrowingFunction<Throwable, ? extends Future<V>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault0 = fault;
        try {
            return mapper.apply(fault0);
        }
        catch (final Throwable fault1) {
            fault1.addSuppressed(fault0);
            fault0 = fault1;
        }
        return Future.fault(fault0);
    }

    @Override
    public <T extends Throwable> Future<V> flatMapCatch(
        final Class<T> class_,
        final ThrowingFunction<T, ? extends Future<V>> mapper
    ) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault0 = fault;
        if (class_.isAssignableFrom(fault0.getClass())) {
            try {
                return mapper.apply(class_.cast(fault0));
            }
            catch (final Throwable fault1) {
                fault1.addSuppressed(fault0);
                fault0 = fault1;
            }
        }
        return Future.fault(fault0);
    }

    @Override
    public Future<V> flatMapFault(final ThrowingFunction<Throwable, ? extends Future<Throwable>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        var fault0 = fault;
        try {
            final var future0 = mapper.apply(fault0);
            final var future1 = new FutureConsumption<V>(this);
            future0.onResult(result -> future1.consume(Result.ofFault(result.hasValue()
                ? result.value()
                : result.fault())));
            return future1;
        }
        catch (final Throwable fault1) {
            fault1.addSuppressed(fault0);
            fault0 = fault1;
        }
        return Future.fault(fault0);
    }

    @Override
    public <T extends Throwable> Future<V> flatMapFault(
        final Class<T> class_,
        final ThrowingFunction<T, ? extends Future<Throwable>> mapper
    ) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        var fault0 = fault;
        if (class_.isAssignableFrom(fault0.getClass())) {
            try {
                final var future0 = mapper.apply(class_.cast(fault0));
                final var future1 = new FutureConsumption<V>(this);
                future0.onResult(result -> future1.consume(Result.ofFault(result.hasValue()
                    ? result.value()
                    : result.fault())));
                return future1;
            }
            catch (final Throwable fault1) {
                fault1.addSuppressed(fault0);
                fault0 = fault1;
            }
        }
        return Future.fault(fault0);
    }

    @Override
    public <U> Future<U> flatMapResult(final ThrowingFunction<Result<V>, ? extends Future<U>> mapper) {
        try {
            return mapper.apply(Result.ofFault(fault));
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
    }

    @Override
    public Future<V> flatMapThrow(final ThrowingFunction<V, ? extends Future<? extends Throwable>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return this;
    }

    @Override
    public <U> Future<U> pass(final U value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        return Future.fault(fault);
    }

    @Override
    public <U> Future<U> fail(final Throwable throwable) {
        if (throwable == null) {
            throw new NullPointerException("throwable");
        }
        throwable.addSuppressed(fault);
        return Future.fault(throwable);
    }

    @Override
    public Future<V> delay(final Duration duration) {
        if (duration == null) {
            throw new NullPointerException("duration");
        }
        final var future = new FutureConsumptionWithExtraCancelTarget<V>(this);
        future.extraCancelTarget(Schedulers.fixed()
            .schedule(duration, () -> future.consume(Result.ofFault(fault))));
        return future;
    }

    @Override
    public Future<V> delayUntil(final Instant baseline) {
        if (baseline == null) {
            throw new NullPointerException("baseline");
        }
        final var result = Result.<V>ofFault(fault);
        final var future = new FutureConsumptionWithExtraCancelTarget<V>(this);
        final var duration = Duration.between(baseline, Instant.now());
        if (duration.isNegative() || duration.isZero()) {
            future.consume(result);
        }
        else {
            future.extraCancelTarget(Schedulers.fixed()
                .schedule(duration, () -> future.consume(result)));
        }
        return future;
    }

    @Override
    public Future<?> fork(final Consumer<V> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        return Future.fault(fault);
    }

    @Override
    public <U> Future<U> forkJoin(final ThrowingFunction<V, U> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return Future.fault(fault);
    }

    @Override
    public V await() {
        throwFault();
        return null;
    }

    @Override
    public V await(final Duration timeout) {
        throwFault();
        return null;
    }

    @SuppressWarnings("unchecked")
    private <E extends Throwable> void throwFault() throws E {
        throw (E) fault;
    }

    @Override
    public String toString() {
        return "Future{fault=" + fault + '}';
    }
}