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
     *                                being available and no new can be created.
     */
    Buffer allocateDynamic();

    /**
     * Allocates a free {@link Buffer} with no less than {@code
     * initialCapacity}. The buffer will expand beyond its initial capacity as
     * needed to fit whatever contents it is provided.
     *
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails due to no free buffer
     *                                being available and no new can be created.
     */
    default Buffer allocateDynamic(int initialCapacity) {
        final var buffer = allocateDynamic();
        buffer.capacity(initialCapacity);
        return buffer;
    }

    /**
     * Allocates a free {@link Buffer} with no less than {@code
     * initialCapacity}. The buffer will not be expanded beyond its initial
     * capacity, even if given more data than it can fit.
     *
     * @return Free {@link Buffer}.
     * @throws BufferAllocationFailed If allocation fails due to no free buffer
     *                                being available and no new can be created.
     */
    Buffer allocateFixed(int fixedCapacity);
}
