package se.arkalix.io;

import se.arkalix.io.buffer.BufferReader;
import se.arkalix.io.buffer.BufferWriter;
import se.arkalix.util.concurrent.Future;

import java.nio.file.Path;
import java.util.Optional;

public interface File extends AutoCloseable {
    Path path();

    Future<Reader> read();

    Future<Writer> write();

    @Override
    void close();

    interface Reader extends BufferReader {
        Future<?> flush();
    }

    interface Writer extends BufferWriter {
        Future<?> flush();
    }

    class Options {
        private Path path;
        private boolean isAppending;
        private boolean isCreatedIfMissing;
        private boolean isCreatedOrFails;
        private boolean isDirectOrFails;
        private boolean isReadable;
        private boolean isSparseIfSupported;
        private boolean isTemporary;
        private boolean isTruncated;
        private boolean isWritable;

        public Optional<Path> path() {
            return Optional.ofNullable(path);
        }

        public Options path(final Path path) {
            this.path = path;
            return this;
        }

        public Options append() {
            isAppending = true;
            return this;
        }

        public Options createIfMissing() {
            isCreatedIfMissing = true;
            return this;
        }

        public Options createOrFail() {
            isCreatedOrFails = true;
            return this;
        }

        public Options directOrFail() {
            isDirectOrFails = true;
            return this;
        }

        public boolean isAppending() {
            return isAppending;
        }

        public boolean isCreatedOrFails() {
            return isCreatedOrFails;
        }

        public boolean isCreatedIfMissing() {
            return isCreatedIfMissing;
        }

        public boolean isDirectOrFails() {
            return isDirectOrFails;
        }

        public boolean isReadable() {
            return isReadable;
        }

        public boolean isSparseIfSupported() {
            return isSparseIfSupported;
        }

        public boolean isTemporary() {
            return isTemporary;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public boolean isWritable() {
            return isWritable;
        }

        public Options readable() {
            this.isReadable = true;
            return this;
        }

        public Options sparseIfSupported() {
            isSparseIfSupported = true;
            return this;
        }

        public Options temporary() {
            isTemporary = true;
            return this;
        }

        public Options truncate() {
            isTruncated = true;
            return this;
        }

        public Options writable() {
            isWritable = true;
            return this;
        }

        public Future<File> open() {
            return EventLoop.main().open(this);
        }
    }
}
