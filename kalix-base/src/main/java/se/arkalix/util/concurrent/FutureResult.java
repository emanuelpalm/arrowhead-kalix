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
 * A {@code Future} that always completes with a predetermined result.
 *
 * @param <V> Type of value that is included if the result is successful.
 */
class FutureResult<V> implements Future<V> {
    private final Result<V> result;

    /**
     * Creates new {@link Future} that always completes with the given
     * {@code result}.
     *
     * @param result Result to include in {@code Future}.
     */
    public FutureResult(final Result<V> result) {
        this.result = result;
    }

    @Override
    public void onResult(final Consumer<Result<V>> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        consumer.accept(result);
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
        if (result.hasFault()) {
            consumer.accept(result.fault());
        }
    }

    @Override
    public Future<V> ifValue(final ThrowingConsumer<V> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        if (result.hasValue()) {
            try {
                consumer.accept(result.value());
            }
            catch (final Throwable fault0) {
                return Future.fault(fault0);
            }
        }
        return this;
    }

    @Override
    public Future<V> ifFault(final ThrowingConsumer<Throwable> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        if (result.hasFault()) {
            final var fault = result.fault();
            try {
                consumer.accept(fault);
            }
            catch (final Throwable fault0) {
                fault0.addSuppressed(fault);
                return Future.fault(fault0);
            }
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
        if (result.hasFault()) {
            final var fault = result.fault();
            if (class_.isAssignableFrom(fault.getClass())) {
                try {
                    consumer.accept(class_.cast(fault));
                }
                catch (final Throwable fault0) {
                    fault0.addSuppressed(fault);
                    return Future.fault(fault0);
                }
            }
        }
        return this;
    }

    @Override
    public Future<V> always(final ThrowingConsumer<Result<V>> consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        try {
            consumer.accept(result);
        }
        catch (final Throwable fault0) {
            if (result.hasFault()) {
                fault0.addSuppressed(result.fault());
            }
            return Future.fault(fault0);
        }
        return this;
    }

    @Override
    public <U> Future<U> map(final ThrowingFunction<? super V, U> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault;
        if (result.hasValue()) {
            try {
                return Future.value(mapper.apply(result.value()));
            }
            catch (final Throwable throwable) {
                fault = throwable;
            }
        }
        else {
            fault = result.fault();
        }
        return Future.fault(fault);
    }

    @Override
    public Future<V> mapCatch(final ThrowingFunction<Throwable, ? extends V> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        if (result.hasValue()) {
            return this;
        }
        var fault = result.fault();
        try {
            return Future.value(mapper.apply(fault));
        }
        catch (final Throwable fault0) {
            fault.addSuppressed(fault0);
            fault = fault0;
        }
        return Future.fault(fault);
    }

    @Override
    public <T extends Throwable> Future<V> mapCatch(
        final Class<T> class_,
        final ThrowingFunction<T, ? extends V> mapper
    ) {
        if (class_ == null) {
            throw new NullPointerException("class_");
        }
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        if (result.hasValue()) {
            return this;
        }
        var fault = result.fault();
        if (class_.isAssignableFrom(fault.getClass())) {
            try {
                return Future.value(mapper.apply(class_.cast(fault)));
            }
            catch (final Throwable fault0) {
                fault0.addSuppressed(fault);
                fault = fault0;
            }
        }
        return Future.fault(fault);
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
        if (result.hasValue()) {
            return this;
        }
        var fault = result.fault();
        if (class_.isAssignableFrom(fault.getClass())) {
            try {
                fault = mapper.apply(class_.cast(fault));
            }
            catch (final Throwable fault0) {
                fault0.addSuppressed(fault);
                fault = fault0;
            }
        }
        return Future.fault(fault);
    }

    @Override
    public <U> Future<U> mapResult(final ThrowingFunction<Result<V>, Result<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        try {
            return Future.of(mapper.apply(result));
        }
        catch (final Throwable fault0) {
            return Future.fault(fault0);
        }
    }

    @Override
    public Future<V> mapThrow(final ThrowingFunction<? super V, Throwable> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault;
        if (result.hasValue()) {
            try {
                fault = mapper.apply(result.value());
            }
            catch (final Throwable throwable) {
                fault = throwable;
            }
        }
        else {
            fault = result.fault();
        }
        return Future.fault(fault);
    }

    @Override
    public <U> Future<U> flatMap(final ThrowingFunction<? super V, ? extends Future<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        Throwable fault;
        if (result.hasValue()) {
            try {
                return mapper.apply(result.value());
            }
            catch (final Throwable throwable) {
                fault = throwable;
            }
        }
        else {
            fault = result.fault();
        }
        return Future.fault(fault);
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
        if (result.hasValue()) {
            return this;
        }
        var fault = result.fault();
        if (class_.isAssignableFrom(fault.getClass())) {
            try {
                return mapper.apply(class_.cast(fault));
            }
            catch (final Throwable fault0) {
                fault0.addSuppressed(fault);
                fault = fault0;
            }
        }
        return Future.fault(fault);
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
        if (result.hasValue() || !class_.isAssignableFrom(result.fault().getClass())) {
            return this;
        }
        try {
            final var future0 = mapper.apply(class_.cast(result.fault()));
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
    public <U> Future<U> flatMapResult(final ThrowingFunction<Result<V>, ? extends Future<U>> mapper) {
        try {
            return mapper.apply(result);
        }
        catch (final Throwable fault) {
            return Future.fault(fault);
        }
    }

    @Override
    public Future<V> flatMapThrow(final ThrowingFunction<V, ? extends Future<? extends Throwable>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        if (result.hasFault()) {
            return this;
        }
        try {
            final var future0 = mapper.apply(result.value());
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
        return result.hasValue()
            ? Future.value(value)
            : Future.fault(result.fault());
    }

    @Override
    public <U> Future<U> fail(final Throwable fault) {
        if (fault == null) {
            throw new NullPointerException("fault");
        }
        if (result.hasFault()) {
            fault.addSuppressed(result.fault());
        }
        return Future.fault(fault);
    }

    @Override
    public Future<V> delay(final Duration duration) {
        if (duration == null) {
            throw new NullPointerException("duration");
        }
        final var future = new FutureConsumptionWithExtraCancelTarget<V>(this);
        future.extraCancelTarget(Schedulers.fixed()
            .schedule(duration, () -> future.consume(result)));
        return future;
    }

    @Override
    public Future<V> delayUntil(final Instant baseline) {
        if (baseline == null) {
            throw new NullPointerException("baseline");
        }
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
        return result.valueOrThrow();
    }

    @Override
    public V await(final Duration timeout) {
        return result.valueOrThrow();
    }

    @Override
    public String toString() {
        return "Future{" +
            (result.hasValue()
                ? "value=" + result.value()
                : "fault=" + result.fault()) +
            '}';
    }
}
