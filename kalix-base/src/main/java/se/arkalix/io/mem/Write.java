package se.arkalix.io.mem;

import se.arkalix.io.buf.BufferReader0;

/**
 * An arbitrary collection of memory that can be written to in sequence.
 */
public interface Write {
    /**
     * Writes as many bytes as possible from {@code source} to this writer.
     *
     * @param source Reader to read bytes from.
     * @return Number of bytes written.
     * @throws NullPointerException If {@code source} is {@code null}.
     */
    int write(Read source);

    /**
     * Writes as many bytes as possible from {@code source} to this writer.
     *
     * @param source Bytes to write.
     * @return Number of bytes written.
     * @throws NullPointerException If {@code source} is {@code null}.
     */
    default int write(final byte[] source) {
        return write(source, 0, source.length);
    }

    /**
     * Writes as many bytes as possible from {@code source} to this writer.
     *
     * @param source       Bytes to write.
     * @param sourceOffset Position in {@code source} at which to start reading
     *                     bytes for writing into this buffer.
     * @param length       Number of bytes to read from {@code source}.
     * @return Number of bytes written.
     * @throws IndexOutOfBoundsException If {@code sourceOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code source}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     */
    default int write(final byte[] source, final int sourceOffset, final int length) {
        return write(BufferReader0.wrap(source, sourceOffset, length));
    }

    /**
     * Repeatedly calls {@link #write(Read)} until no more data remains in
     * {@code source}.
     * <p>
     * If any call to {@link #write(Read)} returns 0 before {@code source} is
     * empty, {@link WriteAllFailed} is thrown.
     *
     * @param source Reader to read bytes from.
     * @throws NullPointerException If {@code source} is {@code null}.
     * @throws WriteAllFailed       If not all bytes in {@code source} can be
     *                              written.
     */
    default <S extends Limit & Offset & Read> void writeAll(final S source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        var bytesToWrite = source.limit() - source.offset();
        while (bytesToWrite > 0) {
            final var bytesWritten = write(source);
            if (bytesWritten <= 0) {
                throw new WriteAllFailed();
            }
            bytesToWrite -= bytesWritten;
        }
    }

    /**
     * Repeatedly calls {@link #write(Read)} until no more data remains in
     * {@code source}.
     * <p>
     * If any call to {@link #write(Read)} returns 0 before {@code source} is
     * empty, {@link WriteAllFailed} is thrown.
     *
     * @param source Bytes to write.
     * @throws NullPointerException If {@code source} is {@code null}.
     * @throws WriteAllFailed       If not all bytes in {@code source} can be
     *                              written.
     */
    default void writeAll(final byte[] source) {
        writeAll(source, 0, source.length);
    }

    /**
     * Repeatedly calls {@link #write(Read)} until no more data remains in
     * {@code source}.
     * <p>
     * If any call to {@link #write(Read)} returns 0 before {@code source} is
     * empty, {@link WriteAllFailed} is thrown.
     *
     * @param source       Bytes to write.
     * @param sourceOffset Position in {@code source} at which to start reading
     *                     bytes for writing into this buffer.
     * @param length       Number of bytes to read from {@code source}.
     * @throws IndexOutOfBoundsException If {@code sourceOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code source}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     * @throws WriteAllFailed            If not all bytes in {@code source} can
     *                                   be written.
     */
    default void writeAll(final byte[] source, final int sourceOffset, final int length) {
        writeAll(BufferReader0.wrap(source, sourceOffset, length));
    }

    default boolean writeByte(final byte b) {
        return write(new byte[]{b}) != 0;
    }
}
