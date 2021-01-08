package se.arkalix.io.fs._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.fs.FileWriter;
import se.arkalix.util.concurrent.Future;

import java.util.Objects;

public class DefaultFileWriter implements FileWriter {
    private FileWriter inner;

    public DefaultFileWriter(final FileWriter inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public Future<?> flushAll() {
        return inner.flushAll();
    }

    @Override
    public Future<?> flushData() {
        return inner.flushData();
    }

    @Override
    public Future<Integer> setAt(final long offset, final BufferReader source, final int sourceOffset, final int length) {
        return inner.setAt(offset, source, sourceOffset, length);
    }

    @Override
    public Future<Integer> write(final BufferReader source, final int sourceOffset, final int length) {
        return inner.write(source, sourceOffset, length);
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
