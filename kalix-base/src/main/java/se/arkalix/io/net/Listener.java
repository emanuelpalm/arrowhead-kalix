package se.arkalix.io.net;

import se.arkalix.util.concurrent.Publisher;
import se.arkalix.util.concurrent.Future;

public interface Listener<S extends Socket> extends Publisher<S> {
    Future<?> close();
}
