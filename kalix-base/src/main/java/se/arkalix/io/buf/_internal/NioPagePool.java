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
    private static final int PAGE_SIZE_NORMAL;
    private static final int PAGE_SIZE_LARGE;
    private static final int PAGE_SIZE_HUGE;
    private static final int PAGE_SIZE_MAX;
    private static final int PAGE_THRESHOLD_REGULAR;
    private static final int PAGE_THRESHOLD_LARGE;
    private static final int PAGE_THRESHOLD_HUGE;

    private final boolean isUsingDirectBuffers;
    private final NioPageList freeListRegular;
    private final NioPageList freeListLarge;
    private final NioPageList freeListHuge;

    public static NioPagePool createDirect() {
        return new NioPagePool(true);
    }

    public static NioPagePool createHeap() {
        return new NioPagePool(false);
    }

    private NioPagePool(final boolean isUsingDirectBuffers) {
        this.isUsingDirectBuffers = isUsingDirectBuffers;
        freeListRegular = new NioPageList(isUsingDirectBuffers, PAGE_SIZE_NORMAL, PAGE_THRESHOLD_REGULAR);
        freeListLarge = new NioPageList(isUsingDirectBuffers, PAGE_SIZE_LARGE, PAGE_THRESHOLD_LARGE);
        freeListHuge = new NioPageList(isUsingDirectBuffers, PAGE_SIZE_HUGE, PAGE_THRESHOLD_HUGE);
    }

    public int allocate(final ArrayList<ByteBuffer> destination, int sizeInBytes) {
        if (destination == null) {
            throw new NullPointerException("destination");
        }
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("sizeInBytes (" + sizeInBytes + ") < 0");
        }

        int actualSizeInBytes = 0;

        if (sizeInBytes > PAGE_SIZE_MAX) {
            actualSizeInBytes = BinaryMath.roundUpToMultipleOfPowerOfTwo(sizeInBytes, PAGE_SIZE_NORMAL)
                .orElseThrow(IllegalStateException::new);

            destination.add(isUsingDirectBuffers
                    ? ByteBuffer.allocateDirect(actualSizeInBytes)
                    : ByteBuffer.allocate(actualSizeInBytes));

            return actualSizeInBytes;
        }

        while (sizeInBytes >= PAGE_SIZE_HUGE) {
            sizeInBytes -= PAGE_SIZE_HUGE;
            actualSizeInBytes += PAGE_SIZE_HUGE;
            destination.add(freeListHuge.popOrAllocateNew());
        }

        while (sizeInBytes >= PAGE_SIZE_LARGE) {
            sizeInBytes -= PAGE_SIZE_LARGE;
            actualSizeInBytes += PAGE_SIZE_LARGE;
            destination.add(freeListLarge.popOrAllocateNew());
        }

        while (sizeInBytes > 0) {
            sizeInBytes -= PAGE_SIZE_NORMAL;
            actualSizeInBytes += PAGE_SIZE_NORMAL;
            destination.add(freeListRegular.popOrAllocateNew());
        }

        return actualSizeInBytes;
    }

    public void free(final ArrayList<ByteBuffer> pages) {
        try {
            for (final var page : pages) {
                final var capacity = page.capacity();

                if (capacity == PAGE_SIZE_NORMAL) {
                    freeListRegular.push(page);
                    continue;
                }

                if (capacity == PAGE_SIZE_LARGE) {
                    freeListLarge.push(page);
                    continue;
                }

                if (capacity == PAGE_SIZE_HUGE) {
                    freeListHuge.push(page);
                    continue;
                }

                free(page);
            }
        }
        finally {
            pages.clear();
        }
    }

    @SuppressWarnings("unused")
    private void free(final ByteBuffer page) {
        // TODO: Make best-effort attempt to free byteBuffer immediately.
    }

    @Override
    public Buffer allocate(final int initialCapacity, final int maximumCapacity) {
        final var buffer = new NioPageBuffer(maximumCapacity, this);
        buffer.writeEnd(initialCapacity);
        return buffer;
    }

    static {
        PAGE_SIZE_NORMAL = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeNormal")
            .orElse(8192);

        PAGE_SIZE_LARGE = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeLarge")
            .orElse(PAGE_SIZE_NORMAL * 8);

        PAGE_SIZE_HUGE = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeHuge")
            .orElse(PAGE_SIZE_LARGE * 8);

        PAGE_SIZE_MAX = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeMax")
            .orElse(PAGE_SIZE_HUGE * 8);

        if (Integer.bitCount(PAGE_SIZE_NORMAL) != 1 || PAGE_SIZE_NORMAL < 512) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeNormal' " +
                "must be a power of 2 larger than or equal to 512");
        }
        if (Integer.bitCount(PAGE_SIZE_LARGE) != 1 || PAGE_SIZE_LARGE < PAGE_SIZE_NORMAL) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeLarge' " +
                "must be a power of 2 larger than or equal to " +
                "'se.arkalix.io.buf.pageSizeNormal'");
        }
        if (Integer.bitCount(PAGE_SIZE_HUGE) != 1 || PAGE_SIZE_HUGE < PAGE_SIZE_LARGE) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeHuge' " +
                "must be a power of 2 larger than or equal to " +
                "'se.arkalix.io.buf.pageSizeLarge'");
        }
        if (Integer.bitCount(PAGE_SIZE_MAX) != 1 || PAGE_SIZE_MAX < PAGE_SIZE_HUGE) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeMax' " +
                "must be a power of 2 larger than or equal to" +
                "'se.arkalix.io.buf.pageSizeHuge'");
        }

        PAGE_THRESHOLD_REGULAR = SystemProperties.getInteger("se.arkalix.io.buf.pageThresholdRegular")
            .orElse(256);

        PAGE_THRESHOLD_LARGE = SystemProperties.getInteger("se.arkalix.io.buf.pageThresholdLarge")
            .orElse(Math.max(PAGE_THRESHOLD_REGULAR / 4, 1));

        PAGE_THRESHOLD_HUGE = SystemProperties.getInteger("se.arkalix.io.buf.pageThresholdHuge")
            .orElse(Math.max(PAGE_THRESHOLD_LARGE / 4, 1));

        if (PAGE_THRESHOLD_REGULAR < 1) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageThresholdRegular' " +
                "must be larger than or equal to 1");
        }
        if (PAGE_THRESHOLD_LARGE < PAGE_THRESHOLD_REGULAR) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageThresholdLarge' " +
                "must be larger than or equal to 'se.arkalix.io.buf.pageThresholdRegular'");
        }
        if (PAGE_THRESHOLD_HUGE < PAGE_THRESHOLD_LARGE) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageThresholdHuge' " +
                "must be larger than or equal to 'se.arkalix.io.buf.pageThresholdLarge'");
        }
    }
}
