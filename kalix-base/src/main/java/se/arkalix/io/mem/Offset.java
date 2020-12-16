package se.arkalix.io.mem;

/**
 * A region of memory through which a cursor may be moved between its beginning
 * at index 0 and some arbitrary limit.
 */
public interface Offset  {
    /**
     * Gets cursor position, relative to the beginning of the memory region.
     *
     * @return Current cursor position.
     */
    int offset();

    /**
     * Sets new cursor position, relative to the beginning of the memory region.
     *
     * @param offset New cursor position.
     */
    void offset(int offset);

    /**
     * Moves cursor back to beginning of memory region.
     */
    default void rewind() {
        offset(0);
    }
}
