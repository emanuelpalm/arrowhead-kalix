package se.arkalix.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The result of an operation that can either succeed or fail.
 * <p>
 * A {@code Result} may either contain a <i>value</i>, in which case the
 * operation it represents was successful, or a <i>fault</i>, which indicates
 * that the operation failed. The {@link #hasValue()} method is used to
 * determine which of the two situations is the case. The {@link #value()}
 * and {@link #fault()} methods are used to collect the value or fault,
 * respectively.
 * <p>
 * This type is especially appropriate to use when thrown exceptions do not end
 * up at the one calling a routine. This is often the case when dealing with
 * thread pools, as the threads in such pools can often not do anything to
 * meaningfully handle exceptions thrown while they are executing.
 *
 * @param <V> Type of value provided by {@code Result} if successful.
 */
@SuppressWarnings("unused")
public class Result<V> {
    private static final Result<?> DONE = new Result<>(null, null);

    private final V value;
    private final Throwable fault;

    private Result(final V value, final Throwable fault) {
        this.value = value;
        this.fault = fault;
    }

    /**
     * Creates new successful {@code Result} containing given {@code value}.
     *
     * @param value Value.
     * @param <V>   Type of value.
     * @return New {@code Result}.
     */
    public static <V> Result<V> ofValue(final V value) {
        return new Result<>(value, null);
    }

    /**
     * Creates new failed {@code Result} containing given {@code fault}.
     *
     * @param fault Reason for failure.
     * @param <V>   Type of value that would have been provided by the
     *              created {@code Result}, if it were successful.
     * @return New {@code Result}.
     * @throws NullPointerException If {@code fault} is {@code null}.
     */
    public static <V> Result<V> ofFault(final Throwable fault) {
        if (fault == null) {
            throw new NullPointerException("fault");
        }
        return new Result<>(null, fault);
    }

    /**
     * Creates new successful {@code Result} with {@code null} value.
     *
     * @return New {@code Result}.
     */
    @SuppressWarnings("unchecked")
    public static <V> Result<V> done() {
        return (Result<V>) DONE;
    }

    /**
     * Determines whether this {@code Result} contains a value.
     *
     * @return {@code true} only if this {@code Result} contains a value.
     */
    public boolean hasValue() {
        return fault == null;
    }

    /**
     * Determines whether this {@code Result} contains a fault.
     *
     * @return {@code true} if only this {@code Result} contains a fault.
     */
    public boolean hasFault() {
        return fault != null;
    }

    /**
     * Gets fault of this {@code Result}, if any.
     *
     * @return A {@code Throwable} if this {@code Result} has a fault or
     * {@code null} in any other case.
     */
    public Throwable fault() {
        return fault;
    }

    /**
     * Gets value of this {@code Result}, if any.
     * <p>
     * Note that {@code null} is a valid value, which means that this method
     * cannot be reliably used to determine whether or not an arbitrary
     * {@code Result} is successful. Rather use {@link #hasValue() or
     * {@link #hasFault()} to determine success.
     *
     * @return Some value if this {@code Result} has a value or {@code null}
     * in any other case.
     */
    public V value() {
        return value;
    }

    /**
     * Either returns {@code Result} value or throws its fault, depending
     * on whether it contains a value or fault.
     *
     * @return Result value, if this {@code Result} contains one.
     */
    public V valueOrThrow() {
        if (hasValue()) {
            return value();
        }
        throwFault();
        return null;
    }

    @SuppressWarnings("unchecked")
    private <E extends Throwable> void throwFault() throws E {
        throw (E) fault();
    }

    /**
     * Calls given {@code consumer} only if this result is successful.
     *
     * @param consumer Consumer function to call, if successful.
     */
    public void ifValue(final Consumer<? super V> consumer) {
        if (hasValue()) {
            consumer.accept(value());
        }
    }

    /**
     * Calls given {@code consumer} only if this result is a fault.
     *
     * @param consumer Consumer function to call, if not successful.
     */
    public void ifFault(final Consumer<Throwable> consumer) {
        if (!hasValue()) {
            consumer.accept(fault());
        }
    }

    /**
     * If this result is successful, applies given {@code mapper} to its value.
     * Otherwise, a new {@code Result} with the fault contained in this one is
     * returned.
     *
     * @param <U>    Type of return value of {@code mapper}.
     * @param mapper Function to apply to result value, if this result is
     *               successful.
     * @return New result containing either output of mapping or an fault
     * passed on from this result.
     */
    public <U> Result<U> map(final Function<? super V, U> mapper) {
        if (hasValue()) {
            return ofValue(mapper.apply(value()));
        }
        return ofFault(fault());
    }

    /**
     * If this result is successful, applies given {@code mapper} to its value,
     * and then returns the {@code Result} returned by the {@code mapper}. If
     * this result is not successful, a new {@code Result} with the fault it
     * contains is returned.
     *
     * @param <U>    Type of value of {@code Result} returned by
     *               {@code mapper}.
     * @param mapper Function to apply to result value, if this result is
     *               successful.
     * @return New result consisting either of result of {@code mapper} or an
     * fault passed on from this result.
     */
    public <U> Result<U> flatMap(final Function<? super V, ? extends Result<U>> mapper) {
        if (hasValue()) {
            return mapper.apply(value());
        }
        return ofFault(fault());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) { return true; }
        if (other == null || getClass() != other.getClass()) { return false; }
        final Result<?> result = (Result<?>) other;
        return Objects.equals(value, result.value) &&
            Objects.equals(fault, result.fault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, fault);
    }

    @Override
    public String toString() {
        return "Result{" + (hasFault() ? "fault=" + fault : "value=" + value) + '}';
    }
}
