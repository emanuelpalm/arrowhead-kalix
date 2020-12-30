package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferAllocator;
import se.arkalix.util._internal.BinaryMath;
import se.arkalix.util._internal.SystemProperties;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Internal
public class NioPagePool implements BufferAllocator {
    private static final int NORMAL_PAGE_SIZE;
    private static final int LARGE_PAGE_SIZE;
    private static final int HUGE_PAGE_SIZE;
    private static final int GIANT_PAGE_SIZE;
    private static final int PAGE_THRESHOLD_REGULAR;
    private static final int PAGE_THRESHOLD_LARGE;
    private static final int PAGE_THRESHOLD_HUGE;

    private final boolean isUsingDirectBuffers;
    private final NioPageList freeListRegular;
    private final NioPageList freeListLarge;
    private final NioPageList freeListHuge;

    public static NioPagePool create() {
        return new NioPagePool(false);
    }

    public static NioPagePool createDirect() {
        return new NioPagePool(true);
    }

    private NioPagePool(final boolean isUsingDirectBuffers) {
        this.isUsingDirectBuffers = isUsingDirectBuffers;
        freeListRegular = new NioPageList(isUsingDirectBuffers, NORMAL_PAGE_SIZE, PAGE_THRESHOLD_REGULAR);
        freeListLarge = new NioPageList(isUsingDirectBuffers, LARGE_PAGE_SIZE, PAGE_THRESHOLD_LARGE);
        freeListHuge = new NioPageList(isUsingDirectBuffers, HUGE_PAGE_SIZE, PAGE_THRESHOLD_HUGE);
    }

    public int allocate(final ArrayList<ByteBuffer> destination, int sizeInBytes) {
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("sizeInBytes (" + sizeInBytes + ") < 0");
        }

        int actualSizeInBytes = 0;

        if (sizeInBytes > GIANT_PAGE_SIZE) {
            actualSizeInBytes = BinaryMath.roundUpToMultipleOfPowerOfTwo(sizeInBytes, NORMAL_PAGE_SIZE)
                .orElseThrow(IllegalStateException::new);

            destination.add(isUsingDirectBuffers
                    ? ByteBuffer.allocateDirect(actualSizeInBytes)
                    : ByteBuffer.allocate(actualSizeInBytes));

            return actualSizeInBytes;
        }

        while (sizeInBytes >= HUGE_PAGE_SIZE) {
            sizeInBytes -= HUGE_PAGE_SIZE;
            actualSizeInBytes += HUGE_PAGE_SIZE;
            destination.add(freeListHuge.popOrAllocate());
        }

        while (sizeInBytes >= LARGE_PAGE_SIZE) {
            sizeInBytes -= LARGE_PAGE_SIZE;
            actualSizeInBytes += LARGE_PAGE_SIZE;
            destination.add(freeListLarge.popOrAllocate());
        }

        while (sizeInBytes > 0) {
            sizeInBytes -= NORMAL_PAGE_SIZE;
            actualSizeInBytes += NORMAL_PAGE_SIZE;
            destination.add(freeListRegular.popOrAllocate());
        }

        return actualSizeInBytes;
    }

    public void free(final ArrayList<ByteBuffer> pages) {
        try {
            for (final var page : pages) {
                final var capacity = page.capacity();

                if (capacity == NORMAL_PAGE_SIZE) {
                    freeListRegular.pushOrFree(page);
                    continue;
                }

                if (capacity == LARGE_PAGE_SIZE) {
                    freeListLarge.pushOrFree(page);
                    continue;
                }

                if (capacity == HUGE_PAGE_SIZE) {
                    freeListHuge.pushOrFree(page);
                    continue;
                }

                ByteBuffers.free(page);
            }
        }
        finally {
            pages.clear();
        }
    }

    @Override
    public Buffer allocate(final int initialCapacity, final int maximumCapacity) {
        if (initialCapacity < 0 || initialCapacity > maximumCapacity) {
            throw new IndexOutOfBoundsException();
        }
        final var buffer = new NioPageBuffer(maximumCapacity, this);
        buffer.writeEnd(initialCapacity);
        return buffer;
    }

    static {
        NORMAL_PAGE_SIZE = SystemProperties.getInteger("se.arkalix.io.buf.normalPageSize")
            .orElse(8192);

        LARGE_PAGE_SIZE = SystemProperties.getInteger("se.arkalix.io.buf.largePageSize")
            .orElse(NORMAL_PAGE_SIZE * 8);

        HUGE_PAGE_SIZE = SystemProperties.getInteger("se.arkalix.io.buf.hugePageSize")
            .orElse(LARGE_PAGE_SIZE * 8);

        GIANT_PAGE_SIZE = SystemProperties.getInteger("se.arkalix.io.buf.giantPageSize")
            .orElse(HUGE_PAGE_SIZE * 8);

        if (Integer.bitCount(NORMAL_PAGE_SIZE) != 1 || NORMAL_PAGE_SIZE < 512) {
            throw new IllegalStateException("'se.arkalix.io.buf.normalPageSize' " +
                "must be a power of 2 larger than or equal to 512");
        }
        if (Integer.bitCount(LARGE_PAGE_SIZE) != 1 || LARGE_PAGE_SIZE < NORMAL_PAGE_SIZE) {
            throw new IllegalStateException("'se.arkalix.io.buf.largePageSize' " +
                "must be a power of 2 larger than or equal to " +
                "'se.arkalix.io.buf.normalPageSize'");
        }
        if (Integer.bitCount(HUGE_PAGE_SIZE) != 1 || HUGE_PAGE_SIZE < LARGE_PAGE_SIZE) {
            throw new IllegalStateException("'se.arkalix.io.buf.hugePageSize' " +
                "must be a power of 2 larger than or equal to " +
                "'se.arkalix.io.buf.largePageSize'");
        }
        if (Integer.bitCount(GIANT_PAGE_SIZE) != 1 || GIANT_PAGE_SIZE < HUGE_PAGE_SIZE) {
            throw new IllegalStateException("'se.arkalix.io.buf.giantPageSize' " +
                "must be a power of 2 larger than or equal to" +
                "'se.arkalix.io.buf.hugePageSize'");
        }

        PAGE_THRESHOLD_REGULAR = SystemProperties.getInteger("se.arkalix.io.buf.regularPurgeInterval")
            .orElse(256);

        PAGE_THRESHOLD_LARGE = SystemProperties.getInteger("se.arkalix.io.buf.largePurgeInterval")
            .orElse(Math.max(PAGE_THRESHOLD_REGULAR / 4, 1));

        PAGE_THRESHOLD_HUGE = SystemProperties.getInteger("se.arkalix.io.buf.hugePurgeInterval")
            .orElse(Math.max(PAGE_THRESHOLD_LARGE / 4, 1));

        if (PAGE_THRESHOLD_REGULAR < 1) {
            throw new IllegalStateException("'se.arkalix.io.buf.regularPurgeInterval' " +
                "must be larger than or equal to 1");
        }
        if (PAGE_THRESHOLD_LARGE < PAGE_THRESHOLD_REGULAR) {
            throw new IllegalStateException("'se.arkalix.io.buf.largePurgeInterval' " +
                "must be larger than or equal to 'se.arkalix.io.buf.regularPurgeInterval'");
        }
        if (PAGE_THRESHOLD_HUGE < PAGE_THRESHOLD_LARGE) {
            throw new IllegalStateException("'se.arkalix.io.buf.hugePurgeInterval' " +
                "must be larger than or equal to 'se.arkalix.io.buf.largePurgeInterval'");
        }
    }
}
