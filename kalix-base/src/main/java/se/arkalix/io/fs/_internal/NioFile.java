package se.arkalix.io.fs._internal;

import se.arkalix.io.IoException;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.io.buf.Buffers;
import se.arkalix.io.evt.Task;
import se.arkalix.io.fs.File;
import se.arkalix.io.fs.FileMetadata;
import se.arkalix.io.fs.FileReader;
import se.arkalix.io.fs.FileWriter;
import se.arkalix.util.concurrent.Future;

import java.io.IOException;
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
        return NioFileMetadata.requestForFileAt(path);
    }

    @Override
    public FileReader reader() {
        return new DefaultFileReader(this);
    }

    @Override
    public FileWriter writer() {
        return new DefaultFileWriter(this);
    }

    @Override
    public Future<Integer> getAt(
        final long offset,
        final BufferWriter destination,
        final int destinationOffset,
        final int length
    ) {
        return new Task.Options<Integer>()
            .action(() -> {
                try {
                    final var channel = Buffers.intoWritableByteChannel(destination);
                    return (int) fileChannel.transferTo(destinationOffset, length, channel);
                }
                catch (final IOException exception) {
                    throw new IoException(exception);
                }
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<Integer> read(final BufferWriter destination, final int destinationOffset, final int length) {
        return new Task.Options<Integer>()
            .action(() -> destination.setAt(destinationOffset, fileChannel, length))
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> flushAll() {
        return new Task.Options<Void>()
            .action(() -> {
                fileChannel.force(true);
                return null;
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> flushData() {
        return new Task.Options<Void>()
            .action(() -> {
                fileChannel.force(false);
                return null;
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<Integer> setAt(
        final long offset,
        final BufferReader source,
        final int sourceOffset,
        final int length
    ) {
        return new Task.Options<Integer>()
            .action(() -> {
                try {
                    final var channel = Buffers.intoReadableByteChannel(source);
                    return (int) fileChannel.transferFrom(channel, sourceOffset, length);
                }
                catch (final IOException exception) {
                    throw new IoException(exception);
                }
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<Integer> write(final BufferReader source, final int sourceOffset, final int length) {
        return new Task.Options<Integer>()
            .action(() -> source.getSomeAt(sourceOffset, fileChannel, length))
            .isBlocking(true)
            .schedule();
    }

    @Override
    public void close() {
        try {
            fileChannel.close();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }
}
