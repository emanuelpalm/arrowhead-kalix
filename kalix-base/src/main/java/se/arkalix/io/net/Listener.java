package se.arkalix.io.net;


import se.arkalix.util.concurrent.Future;
import se.arkalix.util.concurrent._internal.FlowPublishers;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

public interface Listener<S extends Socket> extends Flow.Publisher<S> {
    default Future<?> consume(final Consumer<S> consumer) {
        return FlowPublishers.consume(this, consumer);
    }

    void close();
}
