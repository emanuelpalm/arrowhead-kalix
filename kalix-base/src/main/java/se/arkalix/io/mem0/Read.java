package se.arkalix.io.mem;

import se.arkalix.io.buf0.BufferWriter0;

import java.util.Optional;

/**
 * An arbitrary collection of memory that can be read from in sequence.
 */
public interface Read {
    /**
     * Reads as many bytes as possible from this reader to {@code target}.
     *
     * @param target Writer to write read bytes to.
     * @return Number of bytes read.
     * @throws NullPointerException If {@code source} is {@code null}.
     */
    int read(Write target);

    /**
     * Reads as many bytes as possible from this reader to {@code target}.
     *
     * @param target Byte array to write read bytes to.
     * @return Number of bytes read.
     * @throws NullPointerException If {@code source} is {@code null}.
     */
    default int read(final byte[] target) {
        return read(target, 0, target.length);
    }

    /**
     * Reads as many bytes as possible from this reader to {@code target}.
     *
     * @param target       Byte array to write read bytes to.
     * @param targetOffset Position in {@code target} at which to start writing
     *                     bytes read from this buffer.
     * @param length       Number of bytes to write to {@code source}.
     * @return Number of bytes read.
     * @throws IndexOutOfBoundsException If {@code targetOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code target}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     */
    default int read(final byte[] target, final int targetOffset, final int length) {
        return read(BufferWriter0.wrap(target, targetOffset, length));
    }

    default Optional<Byte> readByte() {
        final var buffer = new byte[1];
        return read(buffer) > 0
            ? Optional.of(buffer[0])
            : Optional.empty();
    }

    /**
     * Repeatedly calls {@link #read(Write)} until {@code target} is full.
     * <p>
     * The limit of {@code target} is determined before any bytes are
     * written to it, which means that it will have more available space when
     * this method returns if it expands dynamically.
     * <p>
     * If any call to {@link #read(Write)} returns 0 before {@code target} is
     * full, {@link ReadExactFailed} is thrown.
     *
     * @param target Writer to write read bytes to.
     * @throws NullPointerException If {@code source} is {@code null}.
     * @throws ReadExactFailed      If not enough bytes can be read to fill up
     *                              {@code target}.
     */
    default <T extends Limit & Offset & Write> void readExact(final T target) {
        if (target == null) {
            throw new NullPointerException("target");
        }
        var bytesToRead = target.limit() - target.offset();
        while (bytesToRead > 0) {
            final var bytesRead = read(target);
            if (bytesRead <= 0) {
                throw new ReadExactFailed();
            }
            bytesToRead -= bytesRead;
        }
    }

    /**
     * Repeatedly calls {@link #read(Write)} until {@code target} is full.
     * <p>
     * If any call to {@link #read(Write)} returns 0 before {@code target} is
     * full, {@link ReadExactFailed} is thrown.
     *
     * @param target Byte array to write read bytes to.
     * @throws NullPointerException If {@code source} is {@code null}.
     * @throws ReadExactFailed      If not enough bytes can be read to fill up
     *                              {@code target}.
     */
    default void readExact(final byte[] target) {
        readExact(target, 0, target.length);
    }

    /**
     * Repeatedly calls {@link #read(Write)} until {@code target} is full.
     * <p>
     * If any call to {@link #read(Write)} returns 0 before {@code target} is
     * full, {@link ReadExactFailed} is thrown.
     *
     * @param target       Byte array to write read bytes to.
     * @param targetOffset Position in {@code target} at which to start writing
     *                     bytes read from this buffer.
     * @param length       Number of bytes to write to {@code source}.
     * @throws IndexOutOfBoundsException If {@code targetOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code target}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     * @throws ReadExactFailed      If not enough bytes can be read to fill up
     *                              {@code target}.
     */
    default void readExact(final byte[] target, final int targetOffset, final int length) {
        readExact(BufferWriter0.wrap(target, targetOffset, length));
    }
}
