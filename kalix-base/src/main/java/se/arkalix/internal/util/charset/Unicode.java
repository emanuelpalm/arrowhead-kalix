package se.arkalix.internal.util.charset;

import se.arkalix.util.annotation.Internal;

import java.io.ByteArrayOutputStream;

/**
 * Various Unicode-related utilities.
 */
@Internal
public class Unicode {
    private Unicode() {}

    /**
     * Writes provided {@code char} as UTF-8 to given {@code stream}.
     *
     * @param c      Character to convert and write.
     * @param stream Target output stream.
     */
    public static void writeAsUtf8To(char c, final ByteArrayOutputStream stream) {
        writeAsUtf8To((int) c, stream);
    }

    /**
     * Writes provided Unicode code point as UTF-8 to given {@code stream}.
     *
     * @param cp     Code point to convert and write.
     * @param stream Target output stream.
     */
    public static void writeAsUtf8To(int cp, final ByteArrayOutputStream stream) {
        if (cp < 0) {
            throw new IllegalArgumentException("Negative code point provided");
        }
        else if (cp < 0x80) {
            stream.write(cp);
        }
        else {
            final var bytes = new byte[6];
            var b0 = 0;
            var prefix = 0xC0;
            var mask = 0x1F;
            while (true) {
                var b = 0x80 | (cp & 0x3F);
                bytes[b0++] = (byte) b;
                cp >>= 6;

                if ((cp & ~mask) == 0) {
                    bytes[b0++] = (byte) (prefix | cp);
                    break;
                }

                prefix = 0x80 | (prefix >> 1);
                mask >>= 1;
            }
            stream.write(bytes, 0, b0);
        }
    }

    /**
     * Writes provided Unicode code point as UTF-8 to given {@code target} byte
     * array from specified {@code offset}, without making any bounds checking.
     *
     * @param cp     Code point to convert and write.
     * @param target Byte array to write result to.
     * @param offset Offset from beginning of byte array from which writing
     *               will begin.
     */
    public static int writeAsUtf8To(int cp, final byte[] target, final int offset) {
        if (cp < 0) {
            throw new IllegalArgumentException("Negative code point provided");
        }
        else if (cp < 0x80) {
            target[offset] = (byte) cp;
            return 1;
        }
        else {
            var b0 = offset;
            var prefix = 0xC0;
            var mask = 0x1F;
            while (true) {
                var b = 0x80 | (cp & 0x3F);
                target[b0++] = (byte) b;
                cp >>= 6;

                if ((cp & ~mask) == 0) {
                    target[b0++] = (byte) (prefix | cp);
                    break;
                }

                prefix = 0x80 | (prefix >> 1);
                mask >>= 1;
            }
            return b0 - offset;
        }
    }
}
