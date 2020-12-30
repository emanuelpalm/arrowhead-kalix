package se.arkalix.io.buf;

import se.arkalix.io.buf._internal.NioPagePool;

/**
 * Allows for free {@link Buffer} instances to be allocated.
 */
@FunctionalInterface
public interface BufferAllocator {
    /**
     * Creates new unpooled buffer allocator.
     * <p>
     * Allocators returned by this method make no attempts to reuse buffers
     * after being closed. This behavior makes it especially well-suited for
     * allocating buffers that live indefinitely without being closed, or in
     * other contexts when buffers are allocated infrequently.
     *
     * @return New buffer allocator.
     */
    static BufferAllocator create() {
        return Buffer::allocate;
    }

    /**
     * Creates new unpooled direct-memory buffer allocator.
     * <p>
     * Allocators returned by this method make no attempts to reuse buffers
     * after being closed. This behavior makes it especially well-suited for
     * allocating buffers that live indefinitely without being closed, or in
     * other contexts when buffers are allocated infrequently.
     * <p>
     * Direct-memory buffers use memory that is not managed by the JVM garbage
     * collector, which might allow for some memory copying to be avoided when
     * performing operations that involve the operating system, such as when
     * receiving or sending network messages.
     *
     * @return New direct-memory buffer allocator.
     */
    static BufferAllocator createDirect() {
        return Buffer::allocateDirect;
    }

    /**
     * Creates new buffer allocator that uses buffer pooling to reuse the
     * memory of closed buffers.
     *
     * @return New pooled buffer allocator.
     */
    static BufferAllocator createPooled() {
        return NioPagePool.create();
    }

    /**
     * Creates new direct-memory buffer allocator that uses buffer pooling to
     * reuse the memory of closed buffers.
     * <p>
     * Direct-memory buffers use memory that is not managed by the JVM garbage
     * collector, which might allow for some memory copying to be avoided when
     * performing operations that involve the operating system, such as when
     * receiving or sending network messages.
     *
     * @return New pooled direct-memory buffer allocator.
     */
    static BufferAllocator createPooledDirect() {
        return NioPagePool.createDirect();
    }

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
