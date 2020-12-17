package se.arkalix.io.mem;

import se.arkalix.io.buf.BufferReader;

/**
 * An arbitrary collection of memory that can be written to out of sequence.
 */
public interface WriteAt extends Limit {
    /**
     * Writes as many bytes as possible from {@code source} to this writer, as
     * if its cursor was positioned at {@code offset}. The actual cursor is not
     * modified by this operation.
     *
     * @param source Reader to read bytes from.
     * @param offset Position in writer memory to at which to start writing
     *               {@code source} bytes.
     * @return Number of bytes written.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     */
    int writeAt(Read source, int offset);

    /**
     * Writes as many bytes as possible from {@code source} to this writer, as
     * if its cursor was positioned at {@code offset}. The actual cursor is not
     * modified by this operation.
     *
     * @param source Bytes to write.
     * @param offset Position in writer memory to at which to start writing
     *               {@code source} bytes.
     * @return Number of bytes written.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     */
    default int writeAt(final byte[] source, final int offset) {
        return writeAt(source, 0, source.length, offset);
    }

    /**
     * Writes as many bytes as possible from {@code source} to this writer, as
     * if its cursor was positioned at {@code offset}. The actual cursor is not
     * modified by this operation.
     *
     * @param source       Bytes to write.
     * @param sourceOffset Position in {@code source} at which to start reading
     *                     bytes for writing into this buffer.
     * @param length       Number of bytes to read from {@code source}.
     * @param offset       Position in writer memory to at which to start
     *                     writing {@code source} bytes.
     * @return Number of bytes written.
     * @throws IndexOutOfBoundsException If {@code sourceOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code source}, or if
     *                                   {@code offset} is less than 0 or equal
     *                                   to or larger than {@link #limit()}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     */
    default int writeAt(final byte[] source, final int sourceOffset, final int length, final int offset) {
        return writeAt(BufferReader.wrap(source, sourceOffset, length), offset);
    }

    /**
     * Repeatedly calls {@link #writeAt(Read, int)} until no more data remains
     * in {@code source}, as if the cursor of this writer was positioned at
     * {@code offset}. The actual cursor is not modified by this operation.
     * <p>
     * If any call to {@link #writeAt(Read, int)} returns 0 before
     * {@code source} is empty, {@link WriteAllFailed} is thrown.
     *
     * @param source Reader to read bytes from.
     * @param offset Position in writer memory to at which to start writing
     *               {@code source} bytes.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     * @throws WriteAllFailed            If not all bytes in {@code source} can
     *                                   be written.
     */
    default <S extends Limit & Offset & Read> void writeAllAt(final S source, int offset) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (offset < 0 || offset >= limit()) {
            throw new IndexOutOfBoundsException();
        }
        var bytesToWrite = source.limit() - source.offset();
        while (bytesToWrite > 0) {
            final var bytesWritten = writeAt(source, offset);
            if (bytesWritten <= 0) {
                throw new WriteAllFailed();
            }
            offset += bytesWritten;
            bytesToWrite -= bytesWritten;
        }
    }

    /**
     * Repeatedly calls {@link #writeAt(Read, int)} until no more data remains
     * in {@code source}, as if the cursor of this writer was positioned at
     * {@code offset}. The actual cursor is not modified by this operation.
     * <p>
     * If any call to {@link #writeAt(Read, int)} returns 0 before
     * {@code source} is empty, {@link WriteAllFailed} is thrown.
     *
     * @param source Bytes to write.
     * @param offset Position in writer memory to at which to start writing
     *               {@code source} bytes.
     * @throws IndexOutOfBoundsException If {@code offset} is less than 0 or
     *                                   larger than or equal to
     *                                   {@link #limit()}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     * @throws WriteAllFailed            If not all bytes in {@code source} can
     *                                   be written.
     */
    default void writeAllAt(final byte[] source, final int offset) {
        writeAllAt(source, 0, source.length, offset);
    }

    /**
     * Repeatedly calls {@link #writeAt(Read, int)} until no more data remains
     * in {@code source}, as if the cursor of this writer was positioned at
     * {@code offset}. The actual cursor is not modified by this operation.
     * <p>
     * If any call to {@link #writeAt(Read, int)} returns 0 before
     * {@code source} is empty, {@link WriteAllFailed} is thrown.
     *
     * @param source       Bytes to write.
     * @param sourceOffset Position in {@code source} at which to start reading
     *                     bytes for writing into this buffer.
     * @param length       Number of bytes to read from {@code source}.
     * @param offset       Position in writer memory to at which to start
     *                     writing {@code source} bytes.
     * @throws IndexOutOfBoundsException If {@code sourceOffset} or {@code
     *                                   length} are negative or outside of the
     *                                   boundaries of {@code source}, or if
     *                                   {@code offset} is less than 0 or equal
     *                                   to or larger than {@link #limit()}.
     * @throws NullPointerException      If {@code source} is {@code null}.
     * @throws WriteAllFailed            If not all bytes in {@code source} can
     *                                   be written.
     */
    default void writeAllAt(final byte[] source, final int sourceOffset, final int length, final int offset) {
        writeAllAt(BufferReader.wrap(source, sourceOffset, length), offset);
    }

    default boolean writeByteAt(final byte b, final int offset) {
        return writeAt(new byte[]{b}, offset) != 0;
    }
}
