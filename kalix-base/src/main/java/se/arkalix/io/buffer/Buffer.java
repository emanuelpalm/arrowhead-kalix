package se.arkalix.io.buffer;

public interface Buffer {
    /**
     * Gets the total number of bytes that can be written to this buffer
     * without it having to be expanded, including any bytes already written.
     *
     * @return Buffer capacity, in bytes.
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
     * Increasing the capacity is not supported for all kinds of buffers.
     *
     * @param capacity Desired new buffer capacity, in bytes.
     * @throws BufferCapacityNotIncreased If the desired capacity is larger
     *                                    than the current and the buffer
     *                                    cannot be increased to it.
     * @throws BufferIsSealed             If this buffer is {@link #seal()
     *                                    sealed}.
     */
    void capacity(int capacity);

    /**
     * Gets position in buffer at which the next written byte will be written
     * to.
     *
     * @return Current write offset.
     */
    int offset();

    /**
     * Sets new write offset.
     *
     * @param offset New write offset.
     * @throws IndexOutOfBoundsException  If given {@code offset} is less than
     *                                    0.
     * @throws BufferCapacityNotIncreased If given {@code Offset} is larger than
     *                                    the current capacity and that capacity
     *                                    could not be increased.
     * @throws BufferIsSealed             If this buffer is {@link #seal()
     *                                    sealed}.
     */
    void offset(int offset);

    /**
     * Gets the number of bytes that can still be written before the buffer
     * needs to be expanded.
     * <p>
     * The underlying buffer might be able to allocate more memory as it fills
     * up. The number returned by this method may or may not represent a final
     * amount of remaining space.
     *
     * @return Remaining number of writable bytes.
     */
    int space();

    /**
     * Writes byte to buffer at the current {@link #offset() offset} and then
     * increments that offset.
     *
     * @param b Byte to write.
     * @throws IndexOutOfBoundsException If there is no space left to write to.
     * @throws BufferIsSealed            If this buffer is {@link #seal()
     *                                   sealed}.
     */
    void write(byte b);

    /**
     * Writes {@code bytes} to buffer at the current {@link #offset() offset}
     * and then increases that offset with the length of the given byte array.
     *
     * @param bytes Bytes to write.
     * @throws IndexOutOfBoundsException If less than {@code bytes.length}
     *                                   bytes of space remains in buffer.
     * @throws BufferIsSealed            If this buffer is {@link #seal()
     *                                   sealed}.
     */
    void write(byte[] bytes);

    /**
     * Seals buffer and returns its {@link BufferView view}.
     * <p>
     * A sealed buffer cannot be written to, or be sealed again, until all of
     * its views have been {@link BufferView#release() released}. If multiple
     * views into a single buffer is desired, use the {@link BufferView#clone()}
     * method.
     *
     * @return View into sealed buffer.
     * @throws BufferIsSealed If this buffer already has been sealed and not
     *                        all of its {@link BufferView views} have been
     *                        {@link BufferView#release() released}.
     */
    BufferView seal();
}
