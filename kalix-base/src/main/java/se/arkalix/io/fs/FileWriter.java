package se.arkalix.io.fs;

import se.arkalix.io.ChannelWriter;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.util.concurrent.Future;

public interface FileWriter extends ChannelWriter {
    Future<?> flushDataOnly();

    default Future<?> setAt(final long offset, final BufferReader source) {
        return setAt(offset, source, source.readOffset(), source.readableBytes());
    }

    Future<?> setAt(long offset, BufferReader source, int sourceOffset, int length);

    @Override
    void close();
}
