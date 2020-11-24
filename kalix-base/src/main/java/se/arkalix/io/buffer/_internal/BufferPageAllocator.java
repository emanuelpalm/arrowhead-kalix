package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.annotation.ThreadSafe;

import java.util.Collections;
import java.util.List;

@Internal
public interface BufferPageAllocator {
    @ThreadSafe
    List<Buffer> allocateBuffers(int numberOfBuffers);

    default List<Buffer> allocateMemory(final int numberOfBytes) {
        if (numberOfBytes < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (numberOfBytes == 0) {
            return Collections.emptyList();
        }
        final var numberOfBuffers = (numberOfBytes - 1) / bufferCapacity() + 1;
        return allocateBuffers(numberOfBuffers);
    }

    @ThreadSafe
    int bufferCapacity();
}
