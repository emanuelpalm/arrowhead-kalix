package se.arkalix.io.net;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.util.concurrent.Publisher;
import se.arkalix.util.concurrent.Future;

public interface Socket extends Publisher<BufferReader> {
    default Future<?> writeAndClose(BufferReader bufferReader) {
        return write(bufferReader)
            .always(ignored -> bufferReader.close());
    }

    Future<?> write(BufferReader buffer);

    Future<?> close();
}
