package se.arkalix.io.buffer;

/**
 * A collection of memory that can be read from.
 */
public interface BufferView extends AutoCloseable {
    /**
     * Gets new shallow copy of this buffer view.
     * <p>
     * Cloned buffer readers do not share offset pointers, which means that
     * they can be used in parallel without risks for race conditions. Note,
     * however, that this method is not thread-safe.
     * <p>
     * Every buffer, including any clones, must be {@link #close() closed}
     * exactly once after no longer being in use.
     *
     * @return Shallow buffer view copy.
     * @throws BufferIsClosed If this buffer view has been closed.
     */
    BufferView clone();

    /**
     * Closes this buffer.
     * <p>
     * All buffer readers <b>must</b> be closed once on longer in use.
     * <p>
     * Calling this method additional times after the first has no effect.
     */
    @Override
    void close();

    /**
     * Gets position in buffer from which the next byte will be read.
     *
     * @return Index of next byte to read.
     * @throws BufferIsClosed If this buffer view has been closed.
     */
    int offset();

    /**
     * Sets position in buffer from which the next byte will be read.
     *
     * @param offset New read offset.
     * @throws IndexOutOfBoundsException If given {@code offset} is less than 0
     *                                   or larger than the number of {@link
     *                                   #remainder() remaining readable bytes}.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    void offset(int offset);

    /**
     * Gets byte at the given {@code offset} without incrementing the internal
     * {@link #offset() read offset}.
     *
     * @param offset Position of byte to read.
     * @return Read byte.
     * @throws IndexOutOfBoundsException If given {@code offset} is less than 0
     *                                   or larger than the number of {@link
     *                                   #remainder() readable bytes}.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    byte getByte(int offset);

    /**
     * Gets bytes at the given {@code offset} without incrementing the internal
     * {@link #offset() read offset}. The number of acquired bytes will be
     * the same as the length of {@code target}.
     *
     * @param offset Position of byte to read.
     * @param target Receiver of read bytes.
     * @throws IndexOutOfBoundsException If given {@code offset} is less than 0
     *                                   or larger than the number of {@link
     *                                   #remainder() readable bytes} minus
     *                                   the length of {@code target}.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    default void getBytes(final int offset, final byte[] target) {
        getBytes(offset, target, 0, target.length);
    }

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
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    void getBytes(int offset, byte[] target, int targetOffset, int length);

    /**
     * Gets byte at the internal {@link #offset() read offset}, without
     * incrementing the offset.
     *
     * @return Byte at the internal {@link #offset() read offset}.
     * @throws IndexOutOfBoundsException If there is no byte left to getByte.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    byte peekByte();

    /**
     * Gets byte at the internal {@link #offset() read offset} and
     * increments that offset.
     *
     * @return Byte at the internal {@link #offset() read offset}.
     * @throws IndexOutOfBoundsException If there is no byte left to read.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    byte readByte();

    /**
     * Reads bytes into {@code target}. The number of read bytes will be the
     * same as the length of {@code target}.
     *
     * @param target Receiver of read bytes.
     * @throws IndexOutOfBoundsException If less than {@code target.length}
     *                                   bytes are available for reading.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    default void readBytes(byte[] target) {
        readBytes(target, 0, target.length);
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
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    void readBytes(byte[] target, int targetOffset, int length);

    /**
     * Gets number of bytes remaining to be read in this buffer view.
     *
     * @return Number of bytes currently left to read.
     * @throws BufferIsClosed If this buffer view has been closed.
     */
    default int remainder() {
        return size() - offset();
    }

    /**
     * Gets the total number of bytes that can be read from this buffer view,
     * including any bytes already read.
     *
     * @return Buffer capacity, in bytes.
     * @throws BufferIsClosed If this buffer view has been closed.
     */
    int size();

    /**
     * Increments the internal {@link #offset() read offset} by 1 without
     * getting the byte at the current offset.
     *
     * @throws IndexOutOfBoundsException If there is no byte left to skip.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    default void skipByte() {
        skipBytes(1);
    }

    /**
     * Increments the internal {@link #offset() read offset} by {@code n}.
     *
     * @throws IndexOutOfBoundsException If n is less than 0 or there are less
     *                                   than {@code n} bytes left to skip.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    void skipBytes(int n);
}
