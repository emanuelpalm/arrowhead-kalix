package se.arkalix.io.buffer._internal;

import se.arkalix.io.buffer.Buffer;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.annotation.ThreadSafe;

import java.util.List;

@Internal
public interface FixedSizeBufferAllocator {
    @ThreadSafe
    List<Buffer> allocate(int n);

    @ThreadSafe
    int bufferCapacity();
}
