package se.arkalix.io.mem;

/**
 * A region of memory with a well-known position beyond which no memory
 * operations are allowed.
 */
public interface Limit {
    /**
     * Gets position beyond which no operations are allowed.
     *
     * @return Position of memory region limit.
     */
    int limit();
}
