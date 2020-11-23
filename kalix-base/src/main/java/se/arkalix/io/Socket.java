package se.arkalix.io;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferView;
import se.arkalix.io.buffer.old.ReadableBuffer;
import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public interface Socket extends Flow.Publisher<ReadableBuffer> {
    default Future<?> write(final Buffer buffer) {
        final var view = buffer.seal();
        return write(view)
            .always(ignored -> view.release());
    }

    Future<?> write(BufferView buffer);

    Future<?> close();
}
