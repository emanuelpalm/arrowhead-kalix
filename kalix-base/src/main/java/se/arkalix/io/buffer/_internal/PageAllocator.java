package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.annotation.ThreadSafe;

import java.util.Collections;
import java.util.List;

@Internal
public interface PageAllocator {
    @ThreadSafe
    List<Buffer> allocatePages(int numberOfPages);

    @ThreadSafe
    default List<Buffer> allocateBytes(final int numberOfBytes) {
        if (numberOfBytes < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (numberOfBytes == 0) {
            return Collections.emptyList();
        }
        final var numberOfBuffers = (numberOfBytes - 1) / pageSize() + 1;
        return allocatePages(numberOfBuffers);
    }

    @ThreadSafe
    int pageSize();
}
