package se.arkalix.util._internal;

import se.arkalix.util.annotation.Internal;

@Internal
public class BinaryMath {
    private BinaryMath() {}

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
