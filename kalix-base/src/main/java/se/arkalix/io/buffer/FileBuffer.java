package se.arkalix.io.buffer;

import se.arkalix.util.concurrent.Future;

public interface FileBuffer extends Buffer {
    default Future<?> flush() {
        return flush(0, offset());
    }

    Future<?> flush(int offset, int length);
}
