package se.arkalix.io.buffer;

/**
 * Allows for free {@link Buffer} instances to be allocated.
 */
public interface BufferAllocator {
    /**
     * Allocates a free {@link Buffer} that will expand as needed to fit
     * whatever contents it is provided.
     *
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails due to no free buffer
     *                                being available or cannot be created.
     */
    Buffer allocate();

    /**
     * Allocates a free {@link Buffer} with no less than
     * {@code initialCapacity}. The buffer will expand beyond its initial
     * capacity as needed to fit whatever contents it is provided.
     *
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails due to no free buffer
     *                                being available or cannot be created.
     */
    Buffer allocateWithInitialCapacity(int initialCapacity);

    /**
     * Allocates a free {@link Buffer} with no less than
     * {@code initialCapacity}. The buffer will not be expanded beyond its
     * initial capacity, even if given more data than it can fit.
     *
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails due to no free buffer
     *                                being available or cannot be created.
     */
    Buffer allocateWithFixedCapacity(int fixedCapacity);
}
