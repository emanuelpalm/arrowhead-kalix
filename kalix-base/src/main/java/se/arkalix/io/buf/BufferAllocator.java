package se.arkalix.io.buf;

/**
 * Allows for free {@link Buffer} instances to be allocated.
 */
public interface BufferAllocator {
    /**
     * Allocates a free {@link Buffer}.
     * <p>
     * The buffer will initially have at least {@code initialCapacity} bytes of
     * internal capacity. If {@code maximumCapacity} is larger than {@code
     * initialCapacity}, the buffer will be expanded as needed to fit any
     * additionally written data until the {@code maximumCapacity} is exceeded.
     *
     * @param initialCapacity Desired initial buffer capacity, in bytes.
     * @param maximumCapacity Desired maximum buffer capacity, in bytes.
     * @return Free {@link Buffer}.
     * @throws IndexOutOfBoundsException If {@code initialCapacity} or {@code
     *                                   maximumCapacity} is below 0, or if
     *                                   {@code initialCapacity} is larger than
     *                                   {@code maximumCapacity}.
     */
    Buffer allocate(int initialCapacity, int maximumCapacity);
}
