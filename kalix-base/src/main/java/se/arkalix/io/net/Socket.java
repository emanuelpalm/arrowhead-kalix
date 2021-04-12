package se.arkalix.io.net;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.util.concurrent.Future;
import se.arkalix.util.concurrent._internal.FlowPublishers;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

public interface Socket extends Flow.Publisher<BufferReader> {
    default Future<?> read(final Consumer<BufferReader> consumer) {
        return FlowPublishers.consume(this, consumer);
    }

    default Future<?> writeAndCloseBuffer(final BufferReader bufferReader) {
        return write(bufferReader)
            .always(ignored -> bufferReader.close());
    }

    Future<?> write(BufferReader buffer);

    void close();
}
