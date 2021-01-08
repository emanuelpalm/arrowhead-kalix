package se.arkalix.io.fs;

import java.time.Instant;
import java.util.Optional;

/**
 * Data about some file residing in some file system.
 */
public interface FileMetadata {
    /**
     * Gets time of file creation, if any such information is stored by the
     * file system in which the file in question resides.
     *
     * @return Time of file creation.
     */
    Optional<Instant> createdAt();

    /**
     * Gets time of last file access, if any such information is stored by the
     * file system in which the file in question resides.
     *
     * @return Time of last file access.
     */
    Optional<Instant> lastAccessedAt();

    /**
     * Gets time of last file modification, if any such information is stored
     * by the file system in which the file in question resides.
     *
     * @return Time of last file modification.
     */
    Optional<Instant> lastModifiedAt();

    /**
     * Gets the byte size of the file represented by this metadata.
     * <p>
     * The returned size is reported as if the file in question is uncompressed,
     * dense and not padded. In other words, the length required for a regular
     * byte array to be able to hold all data of the file.
     * <p>
     * Note, however, that size of each file that is not a {@link #isRegular()
     * regular file} is implementation specific and, therefore, unspecified.
     *
     * @return the file size, in bytes
     */
    long size();

    /**
     * Determines whether or not the file represented by this metadata is a
     * regular file system file.
     *
     * @return {@code true} if the represented file is a regular file, as
     * opposed to being a directory, symbolic link, pipe, or other file system
     * or operating system construct.
     */
    default boolean isRegular() {
        return type() == FileType.REGULAR;
    }

    /**
     * Determines whether or not the file represented by this metadata is a
     * file system directory, able to contain other file system files.
     *
     * @return {@code true} if the represented file is a directory, as opposed
     * to being a regular file, symbolic link, pipe, or other file system or
     * operating system construct.
     */
    default boolean isDirectory() {
        return type() == FileType.DIRECTORY;
    }

    /**
     * Determines whether or not the file represented by this metadata is a
     * symbolic link to another file system file.
     * <p>
     * Note that not all file systems support symbolic links.
     *
     * @return {@code true} if the represented file is a symbolic link, as
     * opposed to being a directory, symbolic link, pipe, or other file system
     * or operating system construct.
     */
    default boolean isSymbolicLink() {
        return type() == FileType.SYMBOLIC_LINK;
    }

    /**
     * Determines whether or not the file represented by this metadata is some
     * kind of file system file that cannot be represented by a {@link
     * FileType}.
     *
     * @return {@code true} if the represented file is something other than a
     * regular file, directory or symbolic link.
     */
    default boolean isOther() {
        return type() == FileType.OTHER;
    }

    /**
     * Gets the type of the file represented by this metadata.
     *
     * @return File type of represented file.
     */
    FileType type();
}
