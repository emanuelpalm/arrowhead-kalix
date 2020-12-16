package se.arkalix.io.net;

import se.arkalix.io._buf.Buffer;
import se.arkalix.io._buf.BufferReader;
import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public interface Socket extends Flow.Publisher<BufferReader> {
    default Future<?> write(Buffer buffer) {
        final var view = buffer.read();
        return write(view)
            .always(ignored -> view.close());
    }

    Future<?> write(BufferReader buffer);

    Future<?> close();
}
