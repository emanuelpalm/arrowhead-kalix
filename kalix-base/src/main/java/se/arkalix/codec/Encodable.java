package se.arkalix.codec;

import se.arkalix.io.buffer.old.WritableBuffer;

/**
 * A function used to encode an implicit class instance into binary data.
 */
@FunctionalInterface
public interface Encodable {
    /**
     * Encodes implicit class instance and writes it to {@code writer}.
     *
     * @param writer Binary data writer.
     * @return Codec applied by this encodable, or {@link CodecType#NONE} if
     * not relevant.
     * @throws NullPointerException If {@code writer} is {@code null}.
     */
    CodecType encode(WritableBuffer writer);
}
