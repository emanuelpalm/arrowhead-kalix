package se.arkalix.io.buffer;

/**
 * An arbitrary collection of memory that can be written to.
 */
public interface BufferWriter extends AutoCloseable {
    /**
     * Gets the total number of bytes that can be written to this buffer
     * without it having to be expanded, including any bytes already written.
     *
     * @return Buffer capacity, in bytes.
     * @throws BufferIsClosed If this buffer is closed.
     */
    int capacity();

    /**
     * Resizes buffer to allow for at least the indicated number of bytes to be
     * written to it, including any bytes already written.
     * <p>
     * Reducing the capacity of a buffer is guaranteed to never fail. If the
     * reduction would make the buffer smaller than {@link #offset()} bytes,
     * the offset is set to the indicated capacity.
     * <p>
     * Increasing the capacity is not supported for all buffer implementations.
     *
     * @param capacity Desired new buffer capacity, in bytes.
     * @throws BufferCapacityNotIncreased If the desired capacity is larger
     *                                    than the current and the buffer
     *                                    cannot be increased to it.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws IndexOutOfBoundsException  If the given {@code capacity} is less
     *                                    than 0.
     */
    void capacity(int capacity);

    /**
     * Closes this buffer and discards its contents.
     * <p>
     * All buffers <b>must</b> be closed once no longer in use, either using
     * this method or any other also closing it, such as {@link Buffer#read()}.
     *
     * @throws BufferIsClosed If this buffer is already closed.
     */
    @Override
    void close();

    /**
     * Gets position in buffer at which the next written byte will be written
     * to.
     *
     * @return Current write offset.
     * @throws BufferIsClosed If this buffer is closed.
     */
    int offset();

    /**
     * Sets new write offset.
     *
     * @param offset New write offset.
     * @throws BufferCapacityNotIncreased If given {@code offset} is larger than
     *                                    the current capacity and that capacity
     *                                    could not be increased.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws IndexOutOfBoundsException  If given {@code offset} is less than
     *                                    0.
     */
    void offset(int offset);

    /**
     * Gets the number of bytes that can still be written before the buffer
     * needs to be expanded or is full.
     * <p>
     * The underlying buffer might be able to allocate more memory as it fills
     * up. The number returned by this method may or may not represent a final
     * amount of remaining space.
     *
     * @return Remaining number of writable bytes.
     */
    default int space() {
        return capacity() - offset();
    }

    /**
     * Writes byte to buffer at the given {@code offset}, without incrementing
     * the internal {@link #offset() write offset}.
     *
     * @param offset Position in buffer where given byte will be written.
     * @param b      Byte to write into this buffer.
     * @throws BufferCapacityNotIncreased If the given {@code offset} is larger
     *                                    than the current {@link #capacity()
     *                                    capacity} and that capacity cannot
     *                                    be increased to accommodate for the
     *                                    given byte.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws IndexOutOfBoundsException  If given {@code offset} is less than
     *                                    0.
     */
    void putByte(int offset, byte b);

    /**
     * Writes {@code source} bytes to buffer at the given {@code offset},
     * without incrementing the internal {@link #offset() write offset}.
     *
     * @param offset Position in buffer where given source bytes are to be
     *               inserted.
     * @param source Source of bytes to copy into this buffer.
     * @throws BufferCapacityNotIncreased If the given {@code offset} added to
     *                                    the given {@code source.length} is
     *                                    larger than the current {@link
     *                                    #capacity() capacity} and that
     *                                    capacity cannot be increased to
     *                                    accommodate bytes to be written.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws IndexOutOfBoundsException  If given {@code offset} is less than
     *                                    0.
     * @throws NullPointerException       If {@code source} is {@code null}.
     */
    default void putBytes(final int offset, final byte[] source) {
        putBytes(offset, source, 0, source.length);
    }

    /**
     * Writes region of {@code source} bytes to buffer at the given {@code
     * offset}, without incrementing the internal {@link #offset() write
     * offset}.
     *
     * @param offset       Position in buffer where given source bytes are to
     *                     be inserted.
     * @param source       Source of bytes to copy into this buffer.
     * @param sourceOffset Position in {@code source} at which to start copying
     *                     bytes.
     * @param length       Number of bytes to copy from {@code source} into
     *                     this buffer.
     * @throws BufferCapacityNotIncreased If the given {@code offset} added to
     *                                    the given {@code length} is larger
     *                                    than the current {@link #capacity()
     *                                    capacity} and that capacity cannot be
     *                                    increased to accommodate for the
     *                                    bytes to be written.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws IndexOutOfBoundsException  If {@code offset}, {@code
     *                                    sourceOffset} or {@code length} is
     *                                    less than 0, or if {@code length}
     *                                    is larger than {@code source.length -
     *                                    sourceOffset}.
     * @throws NullPointerException       If {@code source} is {@code null}.
     */
    void putBytes(int offset, byte[] source, int sourceOffset, int length);

    /**
     * Writes byte to buffer at the current {@link #offset() offset} and then
     * increments that offset.
     *
     * @param b Byte to write.
     * @throws BufferCapacityNotIncreased If the buffer is full and cannot be
     *                                    expanded to make room for the given
     *                                    byte.
     * @throws BufferIsClosed             If this buffer is closed.
     */
    default void writeByte(byte b) {
        final var offset = offset();
        putByte(offset, b);
        offset(offset + 1);
    }

    /**
     * Writes {@code source} to buffer at the current {@link #offset() offset}
     * and then increases that offset with the length of the given byte array.
     *
     * @param source Bytes to write.
     * @throws BufferCapacityNotIncreased If the buffer is full and cannot be
     *                                    expanded to make room for the given
     *                                    source.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws NullPointerException       If {@code source} is {@code null}.
     */
    default void writeBytes(byte[] source) {
        writeBytes(source, 0, source.length);
    }

    /**
     * Writes {@code source} to buffer at the current {@link #offset() offset}
     * and then increases that offset with the length of the given byte array.
     *
     * @param source       Bytes to write.
     * @param sourceOffset Position in {@code source} at which to start read
     *                     bytes for writing into this buffer.
     * @param length       Number of bytes to write to {@code source}.
     * @throws BufferCapacityNotIncreased If the buffer is full and cannot be
     *                                    expanded to make room for the
     *                                    designated region of the given source.
     * @throws BufferIsClosed             If this buffer is closed.
     * @throws IndexOutOfBoundsException  If  {@code sourceOffset} or {@code
     *                                    length} is less than 0, or if {@code
     *                                    length} is larger than {@code
     *                                    source.length - sourceOffset}.
     * @throws NullPointerException       If {@code source} is {@code null}.
     */
    default void writeBytes(final byte[] source, final int sourceOffset, final int length) {
        final var offset = offset();
        putBytes(offset, source, sourceOffset, length);
        offset(offset + length);
    }
}
