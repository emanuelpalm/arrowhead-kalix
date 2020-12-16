package se.arkalix.io.buf._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

@Internal
public abstract class NioPagePool {
    // TODO: Benchmark and optimize the constants in this class.
    // TODO: If possible and performant enough, use some kind of heuristic to adapt these values over time.

    private static final int PAGE_SMALL_DROP_THRESHOLD = 2048;
    private static final int PAGE_SMALL_DROP_HEIGHT = 1024;
    private static final int PAGE_SMALL_SIZE = 2048;                 // 2KB

    private static final int PAGE_LARGE_DROP_THRESHOLD = 384;
    private static final int PAGE_LARGE_DROP_HEIGHT = 256;
    private static final int PAGE_LARGE_SIZE = PAGE_SMALL_SIZE * 32; // 64KB

    private static final int PAGE_GIANT_DROP_THRESHOLD = 160;
    private static final int PAGE_GIANT_DROP_HEIGHT = 64;
    private static final int PAGE_GIANT_SIZE = PAGE_LARGE_SIZE * 32; // 2MB

    private final LinkedList<ByteBuffer> freeSmallPages = new LinkedList<>();
    private final LinkedList<ByteBuffer> freeLargePages = new LinkedList<>();
    private final LinkedList<ByteBuffer> freeGiantPages = new LinkedList<>();

    private int pageSmallDropCountdown = PAGE_SMALL_DROP_THRESHOLD;
    private int pageLargeDropCountdown = PAGE_LARGE_DROP_THRESHOLD;
    private int pageGiantDropCountdown = PAGE_GIANT_DROP_THRESHOLD;

    protected abstract ByteBuffer createBufferOfSize(int sizeInBytes);

    public int allocate(final ArrayList<ByteBuffer> target, int requestedSizeInBytes) {
        if (target == null) {
            throw new NullPointerException("target");
        }
        if (requestedSizeInBytes < 0) {
            throw new IllegalArgumentException();
        }

        int allocatedSizeInBytes = 0;

        while (requestedSizeInBytes > PAGE_GIANT_SIZE) {
            requestedSizeInBytes -= PAGE_GIANT_SIZE;
            allocatedSizeInBytes += PAGE_GIANT_SIZE;

            var byteBuffer = freeGiantPages.poll();
            if (byteBuffer == null) {
                byteBuffer = createBufferOfSize(PAGE_GIANT_SIZE);
            }
            else {
                byteBuffer.clear();
            }

            target.add(byteBuffer);
        }

        while (requestedSizeInBytes > PAGE_LARGE_SIZE) {
            requestedSizeInBytes -= PAGE_LARGE_SIZE;
            allocatedSizeInBytes += PAGE_LARGE_SIZE;

            var byteBuffer = freeLargePages.poll();
            if (byteBuffer == null) {
                byteBuffer = createBufferOfSize(PAGE_LARGE_SIZE);
            }
            else {
                byteBuffer.clear();
            }

            target.add(byteBuffer);
        }

        while (requestedSizeInBytes > 0) {
            requestedSizeInBytes -= PAGE_SMALL_SIZE;
            allocatedSizeInBytes += PAGE_SMALL_SIZE;

            var byteBuffer = freeLargePages.poll();
            if (byteBuffer == null) {
                byteBuffer = createBufferOfSize(PAGE_SMALL_SIZE);
            }
            else {
                byteBuffer.clear();
            }

            target.add(byteBuffer);
        }

        return allocatedSizeInBytes;
    }

    public void recycle(final ArrayList<ByteBuffer> target) {
        if (target == null) {
            throw new NullPointerException("target");
        }

        while (!target.isEmpty()) {
            final var freeBuffer = target.remove(target.size() - 1);

            switch (freeBuffer.capacity()) {
            case PAGE_SMALL_SIZE:
                freeSmallPages.add(freeBuffer);
                if (--pageSmallDropCountdown == 0) {
                    var i = PAGE_SMALL_DROP_HEIGHT;
                    try {
                        final var it = freeSmallPages.iterator();
                        while (it.hasNext() && --i > 0) {
                            it.remove();
                        }
                    }
                    finally {
                        pageSmallDropCountdown = PAGE_SMALL_DROP_THRESHOLD - (PAGE_SMALL_DROP_HEIGHT - i);
                    }
                }
                break;

            case PAGE_LARGE_SIZE:
                freeLargePages.add(freeBuffer);
                if (--pageLargeDropCountdown == 0) {
                    var i = PAGE_LARGE_DROP_HEIGHT;
                    try {
                        final var it = freeLargePages.iterator();
                        while (it.hasNext() && --i > 0) {
                            it.remove();
                        }
                    }
                    finally {
                        pageLargeDropCountdown = PAGE_LARGE_DROP_THRESHOLD - (PAGE_LARGE_DROP_HEIGHT - i);
                    }
                }
                break;

            case PAGE_GIANT_SIZE:
                freeGiantPages.add(freeBuffer);
                if (--pageGiantDropCountdown == 0) {
                    var i = PAGE_GIANT_DROP_HEIGHT;
                    try {
                        final var it = freeGiantPages.iterator();
                        while (it.hasNext() && --i > 0) {
                            it.remove();
                        }
                    }
                    finally {
                        pageGiantDropCountdown = PAGE_GIANT_DROP_THRESHOLD - (PAGE_GIANT_DROP_HEIGHT - i);
                    }
                }
                break;

            default:
                // Ignore.
            }
        }
    }
}

