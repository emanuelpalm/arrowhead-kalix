package se.arkalix.io.fs;

import se.arkalix.io.evt.EventLoop;
import se.arkalix.util.concurrent.Future;

import java.nio.file.Path;
import java.util.Optional;

public interface File extends FileReader, FileWriter {
    Path path();

    Future<FileMetadata> metadata();

    FileReader reader();

    FileWriter writer();

    @Override
    void close();

    class Options {
        private boolean isAppending;
        private boolean isCreating;
        private boolean isCreatingIfMissing;
        private boolean isReadable;
        private boolean isSparseIfSupported;
        private boolean isTemporary;
        private boolean isTruncated;
        private boolean isWritable;
        private Path path;

        public boolean isAppending() {
            return isAppending;
        }

        public Options isAppending(final boolean isAppending) {
            this.isAppending = isAppending;
            return this;
        }

        public boolean isCreating() {
            return isCreating;
        }

        public Options isCreating(final boolean isCreating) {
            this.isCreating = isCreating;
            return this;
        }

        public boolean isCreatingIfMissing() {
            return isCreatingIfMissing;
        }

        public Options isCreatingIfMissing(final boolean isCreatedIfMissing) {
            this.isCreatingIfMissing = isCreatedIfMissing;
            return this;
        }

        public boolean isReadable() {
            return isReadable;
        }

        public Options isReadable(final boolean isReadable) {
            this.isReadable = isReadable;
            return this;
        }

        public boolean isSparseIfSupported() {
            return isSparseIfSupported;
        }

        public Options isSparseIfSupported(final boolean isSparseIfSupported) {
            this.isSparseIfSupported = isSparseIfSupported;
            return this;
        }

        public boolean isTemporary() {
            return isTemporary;
        }

        public Options isTemporary(final boolean isTemporary) {
            this.isTemporary = isTemporary;
            return this;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public Options isTruncated(final boolean isTruncated) {
            this.isTruncated = isTruncated;
            return this;
        }

        public boolean isWritable() {
            return isWritable;
        }

        public Options isWritable(final boolean isWritable) {
            this.isWritable = isWritable;
            return this;
        }

        public Optional<Path> path() {
            return Optional.ofNullable(path);
        }

        public Options path(final Path path) {
            this.path = path;
            return this;
        }

        public Future<File> open() {
            return EventLoop.main().open(this);
        }
    }
}
