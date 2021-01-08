package se.arkalix.io;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.util.concurrent.Future;

public interface ChannelWriter extends AutoCloseable {
    Future<?> flush();

    default Future<?> write(final BufferReader source) {
        return write(source, source.readOffset(), source.readableBytes());
    }

    Future<?> write(BufferReader source, int sourceOffset, int length);

    @Override
    void close();
}
