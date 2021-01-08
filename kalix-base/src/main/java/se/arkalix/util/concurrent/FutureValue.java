package se.arkalix.util.concurrent;

import se.arkalix.util.Result;
import se.arkalix.util.concurrent._internal.FutureConsumption;
import se.arkalix.util.concurrent._internal.FutureConsumptionWithExtraCancelTarget;
import se.arkalix.util.function.ThrowingConsumer;
import se.arkalix.util.function.ThrowingFunction;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

/**
 * A {@code Future} that always succeeds with a predetermined value.
 *
 * @param <V> Type of value.
 */
class FutureValue<V> implements Future<V> {
    static final FutureValue<?> NULL = new FutureValue<>(null);

    private final V value;

    /**
     * Creates new successful {@link Future}.
     *
     * @param value Value to include in {@code Future}.
     */
    public FutureValue(final V value) {
        this.value = value;
    }

    @Override
    public void onResult(final Consumer<Result<V>> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        consumer.accept(Result.ofValue(value));
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
    }

    @Override
    public Future<V> ifValue(final ThrowingConsumer<V> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        try {
            consumer.accept(value);
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
        return this;
    }

    @Override
    public Future<V> ifFault(final ThrowingConsumer<Throwable> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
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
        return this;
    }

    @Override
    public Future<V> always(final ThrowingConsumer<Result<V>> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        try {
            consumer.accept(Result.ofValue(value));
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
        return this;
    }

    @Override
    public <U> Future<U> map(final ThrowingFunction<? super V, U> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        try {
            return Future.value(mapper.apply(value));
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
    }

    @Override
    public Future<V> mapCatch(final ThrowingFunction<Throwable, ? extends V> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return this;
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
        return this;
    }

    @Override
    public Future<V> mapFault(final ThrowingFunction<Throwable, Throwable> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return this;
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
        return this;
    }

    @Override
    public <U> Future<U> mapResult(final ThrowingFunction<Result<V>, Result<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        try {
            return new FutureResult<>(mapper.apply(Result.ofValue(value)));
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
    }

    @Override
    public Future<V> mapThrow(final ThrowingFunction<? super V, Throwable> mapper) {
        Throwable fault;
        try {
            fault = mapper.apply(value);
        }
        catch (final Throwable throwable) {
            fault = throwable;
        }
        return Future.fault(fault);
    }

    @Override
    public <U> Future<U> flatMap(final ThrowingFunction<? super V, ? extends Future<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        try {
            return mapper.apply(value);
        }
        catch (final Throwable throwable) {
            return Future.fault(throwable);
        }
    }

    @Override
    public Future<V> flatMapCatch(final ThrowingFunction<Throwable, ? extends Future<V>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return this;
    }

    @Override
    public <U extends Throwable> Future<V> flatMapCatch(
        final Class<U> class_,
        final ThrowingFunction<U, ? extends Future<V>> mapper
    ) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return this;
    }

    @Override
    public Future<V> flatMapFault(final ThrowingFunction<Throwable, ? extends Future<Throwable>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        return this;
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
        return this;
    }

    @Override
    public <U> Future<U> flatMapResult(final ThrowingFunction<Result<V>, ? extends Future<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        try {
            return mapper.apply(Result.ofValue(value));
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
        try {
            final var future0 = mapper.apply(value);
            final var future1 = new FutureConsumption<V>(this);
            future0.onResult(result -> future1.consume(Result.ofFault(result.hasValue()
                ? result.value()
                : result.fault())));
            return future1;
        }
        catch (final Throwable fault) {
            return Future.fault(fault);
        }
    }

    @Override
    public <U> Future<U> pass(final U value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        return Future.value(value);
    }

    @Override
    public <U> Future<U> fail(final Throwable throwable) {
        if (throwable == null) {
            throw new NullPointerException("throwable");
        }
        return Future.fault(throwable);
    }

    @Override
    public Future<V> delay(final Duration duration) {
        if (duration == null) {
            throw new NullPointerException("duration");
        }
        final var future = new FutureConsumptionWithExtraCancelTarget<V>(this);
        future.extraCancelTarget(Schedulers.fixed()
            .schedule(duration, () -> future.consume(Result.ofValue(value))));
        return future;
    }

    @Override
    public Future<V> delayUntil(final Instant baseline) {
        if (baseline == null) {
            throw new NullPointerException("baseline");
        }
        final var result = Result.ofValue(value);
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
    public V await() {
        return value;
    }

    @Override
    public V await(final Duration timeout) {
        return value;
    }

    @Override
    public String toString() {
        return "Future{value=" + value + '}';
    }
}
