package se.arkalix.util.concurrent._internal;

import se.arkalix.util.Result;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.concurrent.Future;

import java.util.function.Consumer;

@Internal
public class FutureCompletion<V> implements Future<V> {
    private Runnable cancelCallback = null;
    private Consumer<Result<V>> consumer = null;
    private Result<V> result = null;

    private boolean isCancelled = false;
    private boolean isCompleted = false;

    public synchronized boolean isCancelled() {
        return isCancelled;
    }

    public synchronized boolean isCompleted() {
        return isCompleted;
    }

    public synchronized void complete(final Result<V> result) {
        if (isCompleted) {
            throw new IllegalStateException("Already completed");
        }
        if (consumer != null) {
            consumer.accept(result);
            consumer = null;
        }
        else if (!isCancelled) {
            this.result = result;
        }
        isCompleted = true;
    }

    public synchronized void setCancelCallback(final Runnable cancelCallback) {
        if (!isCancelled) {
            this.cancelCallback = cancelCallback;
        }
    }

    @Override
    public synchronized void onResult(final Consumer<Result<V>> consumer) {
        if (result != null) {
            consumer.accept(result);
            result = null;
            isCompleted = true;
        }
        else if (!isCancelled) {
            this.consumer = consumer;
        }
    }

    @Override
    public synchronized void cancel() {
        if (cancelCallback != null) {
            cancelCallback.run();
            cancelCallback = null;
        }
        consumer = null;
        result = null;
        isCancelled = true;
    }
}
