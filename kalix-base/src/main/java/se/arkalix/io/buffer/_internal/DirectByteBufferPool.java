package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Internal
public class DirectByteBufferPool implements BufferPageAllocator {
    // TODO: Benchmark and optimize the constants in this class.
    // TODO: If possible and performant enough, use some kind of heuristic to adapt these values over time.
    private static final int CLEANUP_COUNTDOWN_INITIAL = 2048;
    private static final int CLEANUP_DROP_COUNT = 1024;

    private final ConcurrentLinkedQueue<ByteBuffer> freeList = new ConcurrentLinkedQueue<>();
    private final AtomicInteger cleanupCountdown = new AtomicInteger(CLEANUP_COUNTDOWN_INITIAL);

    @Override
    public List<Buffer> allocateBuffers(int numberOfBuffers) {
        final var buffers = new ArrayList<Buffer>(numberOfBuffers);
        for (; numberOfBuffers > 0; --numberOfBuffers) {
            var byteBuffer = freeList.poll();
            if (byteBuffer == null) {
                byteBuffer = ByteBuffer.allocateDirect(bufferCapacity());
                byteBuffer.order(ByteOrder.nativeOrder());
            }
            else {
                byteBuffer.clear();
            }
            final var byteBuffer0 = byteBuffer;
            buffers.add(new NioBuffer(() -> recycle(byteBuffer0), byteBuffer));
        }
        return buffers;
    }

    private void recycle(final ByteBuffer byteBuffer) {
        final var cleanupCountdown0 = cleanupCountdown.decrementAndGet();

        if (cleanupCountdown0 > 0) {
            freeList.add(byteBuffer);
            return;
        }

        if (cleanupCountdown0 == 0) {
            try {
                var i = CLEANUP_DROP_COUNT;
                final var it = freeList.iterator();
                while (it.hasNext() && --i > 0) {
                    it.remove();
                }
            }
            finally {
                cleanupCountdown.set(CLEANUP_COUNTDOWN_INITIAL - CLEANUP_DROP_COUNT);
            }
        }
    }

    @Override
    public int bufferCapacity() {
        return 2048;
    }
}
