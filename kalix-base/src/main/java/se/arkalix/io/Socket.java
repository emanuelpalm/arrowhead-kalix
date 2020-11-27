package se.arkalix.io;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferReader;
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
