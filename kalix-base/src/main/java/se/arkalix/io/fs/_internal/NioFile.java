package se.arkalix.io.fs._internal;

import se.arkalix.io.IoException;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;
import se.arkalix.io.evt.Task;
import se.arkalix.io.fs.*;
import se.arkalix.util.concurrent.Future;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Objects;

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
    public Future<?> getAt(
        final long offset,
        final BufferWriter destination,
        final int destinationOffset,
        final int length
    ) {
        return new Task.Options<Void>()
            .action(() -> {
                try {
                    final var channel = new BufferAsWritableByteChannel(destination, destinationOffset);

                    int numberOfTransferredBytes = 0;
                    while (numberOfTransferredBytes < length) {
                        final var n = (int) fileChannel.transferTo(offset, length, channel);
                        if (n <= 0) {
                            throw new FileTransferFailed("could only get " + numberOfTransferredBytes + " out of the expected " + length + " bytes from " + path);
                        }
                        numberOfTransferredBytes += n;
                    }

                    return null;
                }
                catch (final IOException exception) {
                    throw new IoException(exception);
                }
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> read(final BufferWriter destination, final int destinationOffset, final int length) {
        return new Task.Options<Void>()
            .action(() -> {
                try {
                    final var channel = new BufferAsWritableByteChannel(destination, destinationOffset);

                    int numberOfTransferredBytes = 0;
                    while (numberOfTransferredBytes < length) {
                        final var n = (int) fileChannel.transferTo(fileChannel.position(), length, channel);
                        if (n <= 0) {
                            throw new FileTransferFailed("could only read " + numberOfTransferredBytes + " out of the expected " + length + " bytes from " + path);
                        }
                        numberOfTransferredBytes += n;
                    }

                    fileChannel.position(fileChannel.position() + length);

                    return null;
                }
                catch (final IOException exception) {
                    throw new IoException(exception);
                }
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> flushDataOnly() {
        return new Task.Options<Void>()
            .action(() -> {
                fileChannel.force(false);
                return null;
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> setAt(
        final long offset,
        final BufferReader source,
        final int sourceOffset,
        final int length
    ) {
        return new Task.Options<Void>()
            .action(() -> {
                try {
                    final var channel = new BufferAsReadableByteChannel(source, sourceOffset);

                    int numberOfTransferredBytes = 0;
                    while (numberOfTransferredBytes < length) {
                        final var n = (int) fileChannel.transferFrom(channel, offset, length);
                        if (n <= 0) {
                            throw new FileTransferFailed("could only set " + numberOfTransferredBytes + " out of the expected " + length + " bytes from " + path);
                        }
                        numberOfTransferredBytes += n;
                    }

                    return null;
                }
                catch (final IOException exception) {
                    throw new IoException(exception);
                }
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> flush() {
        return new Task.Options<Void>()
            .action(() -> {
                fileChannel.force(true);
                return null;
            })
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Future<?> write(final BufferReader source, final int sourceOffset, final int length) {
        return new Task.Options<Void>()
            .action(() -> {
                try {
                    final var channel = new BufferAsReadableByteChannel(source, sourceOffset);

                    int numberOfTransferredBytes = 0;
                    while (numberOfTransferredBytes < length) {
                        final var n = (int) fileChannel.transferFrom(channel, fileChannel.position(), length);
                        if (n <= 0) {
                            throw new FileTransferFailed("could only write " + numberOfTransferredBytes + " out of the expected " + length + " bytes from " + path);
                        }
                        numberOfTransferredBytes += n;
                    }

                    fileChannel.position(fileChannel.position() + length);

                    return null;
                }
                catch (final IOException exception) {
                    throw new IoException(exception);
                }
            })
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

    private static class BufferAsReadableByteChannel implements ReadableByteChannel {
        private final BufferReader source;

        private int offset;

        private BufferAsReadableByteChannel(final BufferReader source, final int offset) {
            this.source = Objects.requireNonNull(source, "source");
            this.offset = offset;
        }

        @Override
        public boolean isOpen() {
            return !source.isClosed();
        }

        @Override
        public void close() {
            source.close();
        }

        @Override
        public int read(final ByteBuffer dst) throws IOException {
            final var numberOfBytesToWrite = Math.min(source.readableBytes(), dst.remaining());
            source.getAt(offset, dst);
            offset += numberOfBytesToWrite;
            return numberOfBytesToWrite;
        }
    }

    private static class BufferAsWritableByteChannel implements WritableByteChannel {
        private final BufferWriter destination;

        private int offset;

        private BufferAsWritableByteChannel(final BufferWriter destination, final int offset) {
            this.destination = Objects.requireNonNull(destination, "destination");
            this.offset = offset;
        }

        @Override
        public boolean isOpen() {
            return !destination.isClosed();
        }

        @Override
        public void close() {
            destination.close();
        }

        @Override
        public int write(final ByteBuffer src) {
            final var numberOfBytesToWrite = Math.min(destination.writableBytes(), src.remaining());
            destination.setAt(offset, src);
            offset += numberOfBytesToWrite;
            return numberOfBytesToWrite;
        }
    }
}
