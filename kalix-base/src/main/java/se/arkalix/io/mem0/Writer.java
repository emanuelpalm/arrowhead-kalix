package se.arkalix.io.mem;

/**
 * Allows for some region of memory to be written to either in or out of
 * sequence.
 */
public interface Writer extends Offset, Write, WriteAt {
    @Override
    default int write(final Read source) {
        final var offset = offset();
        final var length = writeAt(source, offset);
        offset(offset + length);
        return length;
    }
}
