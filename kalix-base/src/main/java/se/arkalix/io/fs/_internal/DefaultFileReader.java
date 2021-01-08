package se.arkalix.io.fs._internal;

import se.arkalix.io.buf.BufferWriter;
import se.arkalix.io.fs.FileReader;
import se.arkalix.util.concurrent.Future;

import java.util.Objects;

public class DefaultFileReader implements FileReader {
    private FileReader inner;

    public DefaultFileReader(final FileReader inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public Future<Integer> getAt(
        final long offset,
        final BufferWriter destination,
        final int destinationOffset,
        final int length
    ) {
        return inner.getAt(offset, destination, destinationOffset, length);
    }

    @Override
    public Future<Integer> read(final BufferWriter destination, final int destinationOffset, final int length) {
        return inner.read(destination, destinationOffset, length);
    }

    @Override
    public void close() {
        try {
            inner.close();
        }
        finally {
            inner = ClosedFile.instance();
        }
    }
}
