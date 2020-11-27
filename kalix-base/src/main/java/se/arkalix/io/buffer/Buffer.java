package se.arkalix.io.buffer;

import se.arkalix.io.buffer._internal.ByteArrayBuffer;

/**
 * An arbitrary collection of memory that can be first written to and then read
 * from.
 * <p>
 * All {@link Buffer Buffers} are write-only. In fact, {@link Buffer Buffers}
 * do not have methods available for reading theirs contents. When a meaningful
 * set of data has been written to a {@link Buffer}, it can be {@link #read()
 * turned into} a {@link BufferReader}, which can only be read.
 */
public interface Buffer extends BufferWriter {
    /**
     * Creates a new buffer wrapping given {@code byteArray}.
     * <p>
     * The caller of this method must ensure that the given byte array is not
     * modified during the lifetime of the returned buffer read. This can be
     * guaranteed by cloning the byte array before providing it, as follows:
     * <pre>
     * final var read = Buffer.wrap(myByteArray.clone());
     * </pre>
     *
     * @param byteArray Byte array to wrap.
     * @return Wrapped byte array.
     * @throws NullPointerException If {@code byteArray} is {@code null}.
     */
    static Buffer wrap(final byte[] byteArray) {
        return new ByteArrayBuffer(byteArray, 0, byteArray.length);
    }

    /**
     * Creates a new buffer wrapping a region of given {@code byteArray}.
     * <p>
     * The caller of this method must ensure that the given byte array is not
     * modified during the lifetime of the returned buffer read. This can be
     * guaranteed by cloning the byte array before providing it, as follows:
     * <pre>
     * final var read = Buffer.wrap(myByteArray.clone(), myOffset, myLength);
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
    static Buffer wrap(final byte[] byteArray, final int offset, final int length) {
        return new ByteArrayBuffer(byteArray, offset, length);
    }

    /**
     * Closes this buffer and returns a {@link BufferReader reader} useful for
     * reading its contents.
     * <p>
     * All buffers <b>must</b> be closed once no longer in use, either using
     * this method or {@link #close()}.
     *
     * @return View into this buffer's contents.
     * @throws BufferIsClosed If this buffer is already closed.
     */
    BufferReader read();
}
