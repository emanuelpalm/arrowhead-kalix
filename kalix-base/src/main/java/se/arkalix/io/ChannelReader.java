package se.arkalix.io;

import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.concurrent.Future;

public interface ChannelReader extends AutoCloseable {
    default Future<?> read(final BufferWriter destination) {
        return read(destination, destination.writeOffset(), destination.writableBytes());
    }

    Future<?> read(BufferWriter destination, int destinationOffset, int length);

    @Override
    void close();
}
