package se.arkalix.io;

import se.arkalix.io.buffer.BufferView;
import se.arkalix.io.buffer.old.ReadableBuffer;
import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public class TcpSocket implements Socket {
    @Override
    public Future<?> write(final BufferView buffer) {
        return null;
    }

    @Override
    public Future<?> close() {
        return null;
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super ReadableBuffer> subscriber) {

    }
}
