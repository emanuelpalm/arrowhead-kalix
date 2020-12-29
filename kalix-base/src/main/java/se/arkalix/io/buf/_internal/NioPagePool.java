package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.Buffer;
import se.arkalix.io.buf.BufferAllocator;
import se.arkalix.util._internal.SystemProperties;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Internal
public class NioPagePool implements BufferAllocator {
    private static final int PAGE_SIZE_REGULAR;
    private static final int PAGE_SIZE_LARGE;
    private static final int PAGE_SIZE_HUGE;
    private static final int PAGE_SIZE_LIMIT;

    private static final int PAGE_THRESHOLD_REGULAR;
    private static final int PAGE_THRESHOLD_LARGE;
    private static final int PAGE_THRESHOLD_HUGE;

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
        freeListRegular = new NioPageList(isUsingDirectBuffers, PAGE_SIZE_REGULAR, PAGE_THRESHOLD_REGULAR);
        freeListLarge = new NioPageList(isUsingDirectBuffers, PAGE_SIZE_LARGE, PAGE_THRESHOLD_LARGE);
        freeListHuge = new NioPageList(isUsingDirectBuffers, PAGE_SIZE_HUGE, PAGE_THRESHOLD_HUGE);
    }

    public int allocate(final ArrayList<ByteBuffer> destination, final int sizeInBytes) {
        return -1;
    }

    public void free(final ArrayList<ByteBuffer> pages) {

    }

    @Override
    public Buffer allocate(final int initialCapacity, final int maximumCapacity) {
        final var buffer = new NioPageBuffer(maximumCapacity, this);
        buffer.writeEnd(initialCapacity);
        return buffer;
    }

    static {
        PAGE_SIZE_REGULAR = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeRegular")
            .orElse(8192);

        PAGE_SIZE_LARGE = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeLarge")
            .orElse(PAGE_SIZE_REGULAR * 8);

        PAGE_SIZE_HUGE = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeHuge")
            .orElse(PAGE_SIZE_LARGE * 8);

        PAGE_SIZE_LIMIT = SystemProperties.getInteger("se.arkalix.io.buf.pageSizeLimit")
            .orElse(PAGE_SIZE_HUGE * 8);

        if (Integer.bitCount(PAGE_SIZE_REGULAR) != 1 || PAGE_SIZE_REGULAR < 512) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeRegular' " +
                "must be a power of 2 larger than or equal to 512");
        }
        if (Integer.bitCount(PAGE_SIZE_LARGE) != 1 || PAGE_SIZE_LARGE < PAGE_SIZE_REGULAR) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeLarge' " +
                "must be a power of 2 larger than or equal to " +
                "'se.arkalix.io.buf.pageSizeRegular'");
        }
        if (Integer.bitCount(PAGE_SIZE_HUGE) != 1 || PAGE_SIZE_HUGE < PAGE_SIZE_LARGE) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeHuge' " +
                "must be a power of 2 larger than or equal to " +
                "'se.arkalix.io.buf.pageSizeLarge'");
        }
        if (Integer.bitCount(PAGE_SIZE_LIMIT) != 1 || PAGE_SIZE_LIMIT < PAGE_SIZE_HUGE) {
            throw new IllegalStateException("'se.arkalix.io.buf.pageSizeLimit' " +
                "must be a power of 2 larger than or equal to" +
                "'se.arkalix.io.buf.pageSizeHuge'");
        }
    }

    static {
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
