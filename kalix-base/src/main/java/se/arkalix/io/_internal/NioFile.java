package se.arkalix.io._internal;

import se.arkalix.io.File;
import se.arkalix.util.concurrent.Future;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class NioFile implements File {
    private final FileChannel channel;
    private final Path path;

    public NioFile(final File.Options options) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Future<Metadata> metadata() {
        return null;
    }

    @Override
    public Path path() {
        return null;
    }

    @Override
    public Future<Permissions> permissions() {
        return null;
    }

    @Override
    public Future<Reader> read() {
        return null;
    }

    @Override
    public Future<Writer> write() {
        return null;
    }

    @Override
    public void close() {
        try {
            channel.close();
        }
        catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
