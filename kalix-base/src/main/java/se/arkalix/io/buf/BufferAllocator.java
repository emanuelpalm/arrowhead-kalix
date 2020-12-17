package se.arkalix.io.buf;

/**
 * Allows for free {@link Buffer} instances to be allocated.
 */
public interface BufferAllocator {
    /**
     * Allocates a free {@link Buffer} with room for no less than {@code
     * capacity} bytes of data. The buffer will not be expanded beyond its
     * initial capacity, even if given more data than it can fit.
     *
     * @param capacity Desired buffer capacity.
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails.
     */
    Buffer allocate(int capacity);

    /**
     * Allocates a free {@link Buffer} that will expand as needed to fit
     * whatever contents it is provided.
     *
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails.
     */
    default Buffer allocateDynamic() {
        return allocateDynamic(0);
    }

    /**
     * Allocates a free {@link Buffer} with room for no less than {@code
     * initialCapacity} bytes of data. The buffer will expand beyond its
     * initial capacity as needed to fit whatever contents it is provided.
     *
     * @param initialCapacity Initial dynamic buffer capacity.
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails.
     */
    Buffer allocateDynamic(int initialCapacity);
}
