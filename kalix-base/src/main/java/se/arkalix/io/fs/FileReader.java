package se.arkalix.io.fs;

import se.arkalix.io.ChannelReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.util.concurrent.Future;

public interface FileReader extends ChannelReader {
    default Future<Integer> getAt(final long offset, final BufferWriter destination) {
        return getAt(offset, destination, destination.writeOffset(), destination.writableBytes());
    }

    Future<Integer> getAt(long offset, BufferWriter destination, int destinationOffset, int length);
}
