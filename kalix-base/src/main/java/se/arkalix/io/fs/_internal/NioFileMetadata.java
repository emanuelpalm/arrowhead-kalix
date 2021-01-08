package se.arkalix.io.fs._internal;

import se.arkalix.io.evt.Task;
import se.arkalix.io.fs.FileMetadata;
import se.arkalix.io.fs.FileType;
import se.arkalix.util.concurrent.Future;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public abstract class NioFileMetadata implements FileMetadata {
    protected abstract BasicFileAttributes attributes();

    public static Future<FileMetadata> requestForFileAt(final Path path) {
        return new Task.Options<FileMetadata>()
            .action(() -> new Basic(Files.readAttributes(path, BasicFileAttributes.class)))
            .isBlocking(true)
            .schedule();
    }

    @Override
    public Optional<Instant> createdAt() {
        final var creationTime = attributes().creationTime();
        final var creationInstant = creationTime.toInstant();
        return creationInstant.getEpochSecond() == 0 && creationInstant.getNano() == 0
            ? Optional.empty()
            : Optional.of(creationInstant);
    }

    @Override
    public Optional<Instant> lastAccessedAt() {
        final var lastAccessTime = attributes().lastAccessTime();
        final var lastAccessInstant = lastAccessTime.toInstant();
        return lastAccessInstant.getEpochSecond() == 0 && lastAccessInstant.getNano() == 0
            ? Optional.empty()
            : Optional.of(lastAccessInstant);
    }

    @Override
    public Optional<Instant> lastModifiedAt() {
        final var lastModifiedTime = attributes().lastModifiedTime();
        final var lastModifiedInstant = lastModifiedTime.toInstant();
        return lastModifiedInstant.getEpochSecond() == 0 && lastModifiedInstant.getNano() == 0
            ? Optional.empty()
            : Optional.of(lastModifiedInstant);
    }

    @Override
    public long size() {
        return attributes().size();
    }

    @Override
    public FileType type() {
        final var attributes = attributes();
        if (attributes.isRegularFile()) {
            return FileType.REGULAR;
        }
        if (attributes.isDirectory()) {
            return FileType.DIRECTORY;
        }
        if (attributes.isSymbolicLink()) {
            return FileType.SYMBOLIC_LINK;
        }
        return FileType.OTHER;
    }

    public static class Basic extends NioFileMetadata {
        private final BasicFileAttributes attributes;

        public Basic(final BasicFileAttributes attributes) {
            this.attributes = Objects.requireNonNull(attributes, "attributes");
        }

        @Override
        protected BasicFileAttributes attributes() {
            return attributes;
        }
    }
}
