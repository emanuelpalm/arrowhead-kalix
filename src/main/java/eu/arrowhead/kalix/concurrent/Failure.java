package eu.arrowhead.kalix.concurrent;

import java.util.function.Consumer;

/**
 * A {@code Future} that always fails with a predetermined error.
 *
 * @param <V> Type of value that would have been included if successful.
 */
public class Failure<V> implements Future<V> {
    private final Throwable error;
    private boolean isDone = false;

    /**
     * Creates new failing {@link Future}.
     *
     * @param error Error to include in {@code Future}.
     */
    public Failure(final Throwable error) {
        this.error = error;
    }

    @Override
    public void onResult(final Consumer<Result<? extends V>> consumer) {
        if (!isDone) {
            consumer.accept(Result.failure(error));
            isDone = true;
        }
    }

    @Override
    public void cancel() {
        isDone = true;
    }

    @Override
    public <U> Future<U> map(final Mapper<? super V, ? extends U> mapper) {
        return new Failure<>(error);
    }

    @Override
    public <U> Future<? extends U> flatMap(final Mapper<? super V, ? extends Future<? extends U>> mapper) {
        return new Failure<>(error);
    }
}
