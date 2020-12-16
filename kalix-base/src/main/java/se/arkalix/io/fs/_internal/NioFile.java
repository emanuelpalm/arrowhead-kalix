package se.arkalix.io.fs._internal;

import se.arkalix.io.fs.File;
import se.arkalix.io.fs.FileMetadata;
import se.arkalix.io.fs.FileReader;
import se.arkalix.io.fs.FileWriter;
import se.arkalix.io.mem.Read;
import se.arkalix.io.mem.Write;
import se.arkalix.util.concurrent.Future;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;

public class NioFile implements File {
    private final FileChannel fileChannel;
    private final Path path;

    public NioFile(final File.Options options) throws IOException {
        final var openOptions = new HashSet<OpenOption>();

        if (options.isAppending()) {
            openOptions.add(StandardOpenOption.APPEND);
        }
        if (options.isCreating()) {
            openOptions.add(StandardOpenOption.CREATE_NEW);
        }
        if (options.isCreatingIfMissing()) {
            openOptions.add(StandardOpenOption.CREATE);
        }
        if (options.isReadable()) {
            openOptions.add(StandardOpenOption.READ);
        }
        if (options.isSparseIfSupported()) {
            openOptions.add(StandardOpenOption.SPARSE);
        }
        if (options.isTemporary()) {
            openOptions.add(StandardOpenOption.DELETE_ON_CLOSE);
        }
        if (options.isTruncated()) {
            openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
        }
        if (options.isWritable()) {
            openOptions.add(StandardOpenOption.WRITE);
        }

        path = options.path().orElseThrow(() -> new NullPointerException("path"));
        fileChannel = FileChannel.open(path, openOptions);
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public Future<FileMetadata> metadata() {
        return null;
    }

    @Override
    public void close() {
        try {
            fileChannel.close();
        }
        catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public FileReader closeAndRead() {
        return null;
    }

    @Override
    public FileWriter closeAndWrite() {
        return null;
    }
}
