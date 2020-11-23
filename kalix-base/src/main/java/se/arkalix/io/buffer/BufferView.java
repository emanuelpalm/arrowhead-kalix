package se.arkalix.io.buffer;

public interface BufferView {
    /**
     * Gets the total number of bytes that can be read from this buffer view,
     * including any bytes already read.
     *
     * @return Buffer capacity, in bytes.
     * @throws BufferIsReleased If this buffer is released.
     */
    int capacity();

    /**
     * Gets new shallow copy of this buffer view.
     * <p>
     * Cloned buffer views do not share offset pointers, which means that they
     * can be used in parallel without risks for race conditions. Note, however,
     * that this method is not thread-safe.
     * <p>
     * Every buffer must be {@link #release() released} exactly once, after no
     * longer being in use.
     *
     * @return Shallow buffer view copy.
     * @throws BufferIsReleased If this buffer is released.
     */
    BufferView clone();

    /**
     * @return Index of next byte to read.
     * @throws BufferIsReleased If this buffer is released.
     */
    int offset();

    /**
     * @param offset New read offset.
     * @throws IndexOutOfBoundsException If given {@code offset} is less than 0
     *                                   or larger than the number of {@link
     *                                   #remaining() readable bytes}.
     * @throws BufferIsReleased          If this buffer is released.
     */
    void offset(int offset);

    /**
     * Gets byte at the internal {@link #offset() read offset}, without
     * incrementing the offset.
     *
     * @return Byte at the internal {@link #offset() read offset}.
     * @throws IndexOutOfBoundsException If there is no byte left to peek.
     * @throws BufferIsReleased          If this buffer is released.
     */
    byte peek();

    /**
     * Gets byte at the given {@code offset} without incrementing the internal
     * {@link #offset() read offset}.
     *
     * @param offset Position of byte to read.
     * @return Read byte.
     * @throws IndexOutOfBoundsException If given {@code offset} is less than 0
     *                                   or larger than the number of {@link
     *                                   #remaining() readable bytes}.
     * @throws BufferIsReleased          If this buffer is released.
     */
    byte peek(int offset);

    /**
     * Gets bytes at the given {@code offset} without incrementing the internal
     * {@link #offset() read offset}. The number of acquired bytes will be
     * the same as the length of {@code target}.
     *
     * @param offset Position of byte to read.
     * @param target Receiver of read bytes.
     * @throws IndexOutOfBoundsException If given {@code offset} is less than 0
     *                                   or larger than the number of {@link
     *                                   #remaining() readable bytes} minus
     *                                   the length of {@code target}.
     * @throws BufferIsReleased          If this buffer is released.
     */
    void peek(int offset, byte[] target);

    /**
     * Gets bytes at the given {@code offset} without incrementing the internal
     * {@link #offset() read offset}.
     *
     * @param offset       Position of byte to read.
     * @param target       Receiver of read bytes.
     * @param targetOffset Offset from beginning of {@code target} at which the
     *                     received bytes will be written.
     * @param length       The number of bytes to read into {@code target}.
     * @throws IndexOutOfBoundsException If {@code  offset}, {@code
     *                                   targetOffset} or {@code length} are
     *                                   negative, out of bounds, or there are
     *                                   less than {@code length} bytes left to
     *                                   read.
     * @throws BufferIsReleased          If this buffer is released.
     */
    void peek(final int offset, final byte[] target, final int targetOffset, final int length);

    /**
     * Gets byte at the internal {@link #offset() read offset} and
     * increments that offset.
     *
     * @return Byte at the internal {@link #offset() read offset}.
     * @throws IndexOutOfBoundsException If there is no byte left to read.
     * @throws BufferIsReleased          If this buffer is released.
     */
    byte read();

    /**
     * Reads bytes into {@code target}. The number of read bytes will be the
     * same as the length of {@code target}.
     *
     * @param target Receiver of read bytes.
     * @throws IndexOutOfBoundsException If less than {@code target.length}
     *                                   bytes are available for reading.
     * @throws BufferIsReleased          If this buffer is released.
     */
    default void read(byte[] target) {
        read(target, 0, target.length);
    }

    /**
     * Reads {@code length} bytes into {@code target}, beginning at
     * {@code targetOffset}.
     *
     * @param target       Receiver of read bytes.
     * @param targetOffset Position in {@code target} at which to start adding
     *                     read bytes.
     * @param length       Number of bytes to read into {@code target}.
     * @throws IndexOutOfBoundsException If {@code targetOffset} or {@code
     *                                   length} are negative, out of bounds,
     *                                   or there are less than {@code length}
     *                                   bytes left to read.
     * @throws BufferIsReleased          If this buffer is released.
     */
    void read(byte[] target, int targetOffset, int length);

    /**
     * Releases this buffer view, making any further attempts to use it cause
     * {@link BufferIsReleased} exceptions to be thrown.
     * <p>
     * All buffer views <b>must</b> be released once on longer in use.
     *
     * @throws BufferIsReleased If this buffer is already released.
     */
    void release();

    /**
     * Gets number of bytes remaining to be read in this buffer view.
     *
     * @return Number of bytes currently left to read.
     * @throws BufferIsReleased If this buffer is released.
     */
    int remaining();

    /**
     * Increments the internal {@link #offset() read offset} by 1 without
     * getting the byte at the current offset.
     *
     * @throws IndexOutOfBoundsException If there is no byte left to skip.
     * @throws BufferIsReleased          If this buffer is released.
     */
    default void skip() {
        skip(1);
    }

    /**
     * Increments the internal {@link #offset() read offset} by {@code n}.
     *
     * @throws IndexOutOfBoundsException If there are less than {@code n} bytes
     *                                   left to skip.
     * @throws BufferIsReleased          If this buffer is released.
     */
    void skip(int n);
}
