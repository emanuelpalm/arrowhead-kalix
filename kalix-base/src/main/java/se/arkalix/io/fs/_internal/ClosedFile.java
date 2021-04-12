package se.arkalix.io.fs._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.io.fs.*;
import se.arkalix.util.concurrent.Future;

import java.nio.file.Path;

public class ClosedFile implements File {
    private static final ClosedFile instance = new ClosedFile();

    public static ClosedFile instance() {
        return instance;
    }

    @Override
    public Path path() {
        throw new FileIsClosed();
    }

    @Override
    public Future<FileMetadata> metadata() {
        throw new FileIsClosed();
    }

    @Override
    public FileReader reader() {
        throw new FileIsClosed();
    }

    @Override
    public FileWriter writer() {
        throw new FileIsClosed();
    }

    @Override
    public Future<Integer> getAt(
        final long offset,
        final BufferWriter destination,
        final int destinationOffset,
        final int length
    ) {
        throw new FileIsClosed();
    }

    @Override
    public Future<Integer> read(final BufferWriter destination, final int destinationOffset, final int length) {
        throw new FileIsClosed();
    }

    @Override
    public Future<?> flushDataOnly() {
        throw new FileIsClosed();
    }

    @Override
    public Future<Integer> setAt(final long offset, final BufferReader source, final int sourceOffset, final int length) {
        throw new FileIsClosed();
    }

    @Override
    public Future<?> flush() {
        throw new FileIsClosed();
    }

    @Override
    public Future<Integer> write(final BufferReader source, final int sourceOffset, final int length) {
        throw new FileIsClosed();
    }

    @Override
    public void close() {
        // Does nothing.
    }
}
