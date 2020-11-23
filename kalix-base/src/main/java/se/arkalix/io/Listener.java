package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public interface Listener<T extends Socket> extends Flow.Publisher<T> {
    Future<?> close();
}
