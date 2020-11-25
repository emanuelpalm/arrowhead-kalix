package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public interface File {
    class Options {
        private Path path;
        private boolean isAppending;
        private boolean isCreatedIfMissing;
        private boolean isCreatedOrFail;
        private boolean isReadable;
        private boolean isSparse;
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

        public boolean isAppending() {
            return isAppending;
        }

        public Options isAppending(final boolean isAppending) {
            this.isAppending = isAppending;
            return this;
        }

        public boolean isCreatedIfMissing() {
            return isCreatedIfMissing;
        }

        public Options isCreatedIfMissing(final boolean isCreatedIfMissing) {
            this.isCreatedIfMissing = isCreatedIfMissing;
            return this;
        }

        public boolean isCreatedOrFail() {
            return isCreatedOrFail;
        }

        public Options isCreatedOrFail(final boolean isCreatedOrFail) {
            this.isCreatedOrFail = isCreatedOrFail;
            return this;
        }

        public boolean isReadable() {
            return isReadable;
        }

        public Options isReadable(final boolean isReadable) {
            this.isReadable = isReadable;
            return this;
        }

        public boolean isSparse() {
            return isSparse;
        }

        public Options isSparse(final boolean isSparse) {
            this.isSparse = isSparse;
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

        public Future<File> open() {
            return EventLoop.main().open(this);
        }
    }
}
