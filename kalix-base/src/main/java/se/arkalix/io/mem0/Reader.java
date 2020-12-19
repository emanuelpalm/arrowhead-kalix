package se.arkalix.io.mem;

/**
 * Allows for some region of memory to be read either in or out of sequence.
 */
public interface Reader extends Offset, Read, ReadAt {
    @Override
    default int read(final Write target) {
        final var offset = offset();
        final var length = readAt(target, offset);
        offset(offset + length);
        return length;
    }
}
