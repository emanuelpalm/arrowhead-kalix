package se.arkalix.io.buffer;

import se.arkalix.io.buffer._internal.ByteArrayBuffer;
import se.arkalix.io.buffer._internal.EmptyBufferView;

/**
 * A collection of memory that can be read from.
 */
public interface BufferView extends AutoCloseable {
    /**
     * Gets a reference to an empty buffer view.
     *
     * @return Empty buffer view.
     */
    static BufferView empty() {
        return EmptyBufferView.instance();
    }

    /**
     * Creates a new buffer view wrapping given {@code byteArray}.
     * <p>
     * The caller of this method must ensure that the given byte array is not
     * modified during the lifetime of the returned buffer view. This can be
     * guaranteed by cloning the byte array before providing it, as follows:
     * <pre>
     * final var view = BufferView.wrap(myByteArray.clone());
     * </pre>
     *
     * @param byteArray Byte array to wrap.
     * @return Wrapped byte array.
     * @throws NullPointerException If {@code byteArray} is {@code null}.
     */
    static BufferView wrap(final byte[] byteArray) {
        return new ByteArrayBuffer.View(byteArray, 0, byteArray.length);
    }

    /**
     * Creates a new buffer view wrapping a region of given {@code byteArray}.
     * <p>
     * The caller of this method must ensure that the given byte array is not
     * modified during the lifetime of the returned buffer view. This can be
     * guaranteed by cloning the byte array before providing it, as follows:
     * <pre>
     * final var view = BufferView.wrap(myByteArray.clone(), myOffset, myLength);
     * </pre>
     *
     * @param byteArray Byte array to wrap.
     * @param offset    Offset from beginning of {@code byteArray} to wrap.
     * @param length    Length, from {@code offset}, to include in wrapping.
     * @return Wrapped byte array.
     * @throws NullPointerException      If {@code byteArray} is {@code null}.
     * @throws IndexOutOfBoundsException If {@code offset} or {@code length} is
     *                                   less than 0, or if {@code offset +
     *                                   length > byteArray.length}.
     */
    static BufferView wrap(final byte[] byteArray, final int offset, final int length) {
        return new ByteArrayBuffer.View(byteArray, offset, length);
    }

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
     * Gets new shallow copy of this buffer view.
     * <p>
     * Duped buffer views have their own offset pointers, initially equal to
     * the views they were created from. Duped buffers can be used in parallel
     * with their originals without risks for race conditions. Note, however,
     * that this method is itself not thread-safe. Any duping must happen while
     * the duped buffer view is not being used by any other thread.
     * <p>
     * Every buffer, including any dupes, must be {@link #close() closed}
     * exactly once after no longer being in use.
     *
     * @return Shallow buffer view copy.
     * @throws BufferIsClosed If this buffer view has been closed.
     */
    BufferView dupe();

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
    default byte peekByte() {
        return getByte(offset());
    }

    /**
     * Gets byte at the internal {@link #offset() read offset} and
     * increments that offset.
     *
     * @return Byte at the internal {@link #offset() read offset}.
     * @throws IndexOutOfBoundsException If there is no byte left to read.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    default byte readByte() {
        final var offset = offset();
        final var b = getByte(offset);
        offset(offset + 1);
        return b;
    }

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
    default void readBytes(byte[] target, int targetOffset, int length) {
        final var offset = offset();
        getBytes(offset, target, targetOffset, length);
        offset(offset + length);
    }

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
     * Increments the internal {@link #offset() read offset} by 1.
     *
     * @throws IndexOutOfBoundsException If there is no byte left to skip.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    default void skip() {
        skip(1);
    }

    /**
     * Increments the internal {@link #offset() read offset} by {@code n}.
     *
     * @throws IndexOutOfBoundsException If n is less than 0 or there are less
     *                                   than {@code n} bytes left to skip.
     * @throws BufferIsClosed            If this buffer view has been closed.
     */
    default void skip(final int n) {
        if (n < 0) {
            throw new IndexOutOfBoundsException();
        }
        offset(offset() + n);
    }
}
