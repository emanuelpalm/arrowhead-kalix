package se.arkalix.io.mem;

import se.arkalix.io.buf.BufferWriter0;

import java.util.Optional;

/**
 * An arbitrary collection of memory that can be read from out of sequence.
 */
public interface ReadAt extends Limit {
    /**
     * Reads as many bytes as possible from this reader to {@code target} as if
     * its cursor was positioned at {@code offset}. The actual cursor is not
     * modified by this operation.
     *
     * @param target Writer to write read bytes to.
     * @param offset Position in reader memory from which to start reading
     *               bytes into {@code target}.
     * @return Number of bytes read.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code target} is {@code null}.
     */
    int readAt(Write target, int offset);

    /**
     * Reads as many bytes as possible from this reader to {@code target} as if
     * its cursor was positioned at {@code offset}. The actual cursor is not
     * modified by this operation.
     *
     * @param target Byte array to write read bytes to.
     * @param offset Position in reader memory from which to start reading
     *               bytes into {@code target}.
     * @return Number of bytes read.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code target} is {@code null}.
     */
    default int readAt(final byte[] target, final int offset) {
        return readAt(target, 0, target.length, offset);
    }

    /**
     * Reads as many bytes as possible from this reader to {@code target} as if
     * its cursor was positioned at {@code offset}. The actual cursor is not
     * modified by this operation.
     *
     * @param target       Byte array to write read bytes to.
     * @param targetOffset Position in {@code target} at which to start writing
     *                     bytes read from this buffer.
     * @param length       Number of bytes to write to {@code target}.
     * @param offset       Position in reader memory from which to start reading
     *                     bytes into {@code target}.
     * @return Number of bytes read.
     * @throws IndexOutOfBoundsException If {@code targetOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code target}, or if
     *                                   {@code offset} is less than 0 or equal
     *                                   to or larger than {@link #limit()}.
     * @throws NullPointerException      If {@code target} is {@code null}.
     */
    default int readAt(final byte[] target, final int targetOffset, final int length, final int offset) {
        return readAt(BufferWriter0.wrap(target, targetOffset, length), offset);
    }

    default Optional<Byte> readByteAt(final int offset) {
        final var buffer = new byte[1];
        return readAt(buffer, offset) > 0
            ? Optional.of(buffer[0])
            : Optional.empty();
    }

    /**
     * Repeatedly calls {@link #readAt(Write, int)} until {@code target} is
     * full, as if the cursor of this writer was positioned at {@code offset}.
     * <p>
     * The limit of {@code target} is determined before any bytes are
     * written to it, which means that it will have more available space when
     * this method returns if it expands dynamically.
     * <p>
     * If any call to {@link #readAt(Write, int)} returns 0 before
     * {@code target} is full, {@link ReadExactFailed} is thrown.
     *
     * @param target Writer to write read bytes to.
     * @param offset Position in reader memory from which to start reading
     *               bytes into {@code target}.
     * @throws NullPointerException If {@code source} is {@code null}.
     * @throws ReadExactFailed      If not enough bytes can be read to fill up
     *                              {@code target}.
     */
    default <T extends Limit & Offset & Write> void readExactAt(final T target, int offset) {
        if (target == null) {
            throw new NullPointerException("target");
        }
        if (offset < 0 || offset >= limit()) {
            throw new IndexOutOfBoundsException();
        }
        var bytesToWrite = target.limit() - target.offset();
        while (bytesToWrite > 0) {
            final var bytesWritten = readAt(target, offset);
            if (bytesWritten <= 0) {
                throw new ReadExactFailed();
            }
            offset += bytesWritten;
            bytesToWrite -= bytesWritten;
        }
    }

    /**
     * Repeatedly calls {@link #readAt(Write, int)} until {@code target} is
     * full, as if the cursor of this writer was positioned at {@code offset}.
     * <p>
     * If any call to {@link #readAt(Write, int)} returns 0 before
     * {@code target} is full, {@link ReadExactFailed} is thrown.
     *
     * @param target Byte array to write read bytes to.
     * @param offset Position in reader memory from which to start reading
     *               bytes into {@code target}.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code target} is {@code null}.
     * @throws ReadExactFailed           If not enough bytes can be read to
     *                                   fill up {@code target}.
     */
    default void readExactAt(final byte[] target, final int offset) {
        readExactAt(target, 0, target.length, offset);
    }

    /**
     * Repeatedly calls {@link #readAt(Write, int)} until {@code target} is
     * full, as if the cursor of this writer was positioned at {@code offset}.
     * <p>
     * If any call to {@link #readAt(Write, int)} returns 0 before
     * {@code target} is full, {@link ReadExactFailed} is thrown.
     *
     * @param target       Byte array to write read bytes to.
     * @param targetOffset Position in {@code target} at which to start writing
     *                     bytes read from this buffer.
     * @param length       Number of bytes to write to {@code target}.
     * @param offset       Position in reader memory from which to start reading
     *                     bytes into {@code target}.
     * @throws IndexOutOfBoundsException If {@code targetOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code target}, or if
     *                                   {@code offset} is less than 0 or equal
     *                                   to or larger than {@link #limit()}.
     * @throws NullPointerException      If {@code target} is {@code null}.
     * @throws ReadExactFailed           If not enough bytes can be read to
     *                                   fill up {@code target}.
     */
    default void readExactAt(final byte[] target, final int targetOffset, final int length, final int offset) {
        readAt(BufferWriter0.wrap(target, targetOffset, length), offset);
    }
}
