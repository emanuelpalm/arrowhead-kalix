package se.arkalix.util._internal;

import se.arkalix.util.annotation.Internal;

@Internal
public class BinaryMath {
    private BinaryMath() {}

    /**
     * Rounds up {@code value} to the next power of two.
     * <p>
     * If {@code value} is negative, zero, or its next power of two is beyond
     * the limit of what a signed 32-bit integer can express, {@code
     * defaultValue} is returned.
     * <p>
     * The original implementation of this function was produced by Sean Eron
     * Anderson (seander@cs.stanford.edu), and can be found on his
     * <a href="https://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2">website</a>.
     *
     * @param value        The integer to round up to its next power of two.
     * @param defaultValue The value to return if rounding fails.
     * @return Rounded up integer or default value.
     */
    public static int roundUpToNextPowerOfTwoOrUseDefault(int value, final int defaultValue) {
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        value++;
        if (value <= 0) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Determines if the range specified by {@code offset} and {@code length}
     * does not reside within the bounds between 0 and {@code capacity}, or if
     * any of {@code offset} or {@code length} is below 0.
     * <p>
     * The implementation of this function has been taken from
     * <i>io.netty.util.internal.MathUtil#isOutOfBounds(int, int, int)</i> of
     * version 4.1 of the <a href="https://netty.io">Netty library</a>, which
     * is covered by the <a href="https://apache.org/licenses/LICENSE-2.0">
     * Apache 2.0 license</a>.
     *
     * @param offset   Start of inner range.
     * @param length   Length of inner range.
     * @param capacity Length outer range.
     * @return {@code true} only if the specified range is out of bounds.
     */
    public static boolean isRangeOutOfBounds(final int offset, final int length, final int capacity) {
        return (offset | length | (offset + length) | (capacity - (offset + length))) < 0;
    }
}
