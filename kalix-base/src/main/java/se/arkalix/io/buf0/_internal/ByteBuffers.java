package se.arkalix.io.buf._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.ByteBuffer;
import java.util.ArrayList;

@Internal
public class ByteBuffers {
    private ByteBuffers() {}

    public static ByteBuffer copy(final ByteBuffer original) {
        if (original == null) {
            throw new NullPointerException("original");
        }

        final ByteBuffer clone = original.isDirect()
            ? ByteBuffer.allocateDirect(original.capacity())
            : ByteBuffer.allocate(original.capacity());

        final ByteBuffer ro = original.asReadOnlyBuffer();
        ro.flip();
        clone.put(ro);

        clone.position(original.position());
        clone.limit(original.limit());
        clone.order(original.order());

        return clone;
    }

    public static ArrayList<ByteBuffer> copy(final ArrayList<ByteBuffer> pages) {
        if (pages == null) {
            throw new NullPointerException("pages");
        }

        final var copiedPages = new ArrayList<ByteBuffer>(pages.size());
        for (final var page : pages) {
            copiedPages.add(copy(page));
        }
        return copiedPages;
    }

    public static ArrayList<ByteBuffer> duplicate(final ArrayList<ByteBuffer> pages) {
        if (pages == null) {
            throw new NullPointerException("pages");
        }

        final var copiedPages = new ArrayList<ByteBuffer>(pages.size());
        for (final var page : pages) {
            copiedPages.add(page.duplicate());
        }
        return copiedPages;
    }
}
