package se.arkalix.io;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.io.buffer.BufferView;
import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public interface Socket extends Flow.Publisher<BufferView> {
    default Future<?> write(Buffer buffer) {
        final var view = buffer.view();
        return write(view)
            .always(ignored -> view.close());
    }

    Future<?> write(BufferView buffer);

    Future<?> close();
}
