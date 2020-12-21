package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;

import java.nio.ByteBuffer;

public class BufferEmpty extends BufferBase {
    private static final BufferEmpty instance = new BufferEmpty();

    public static BufferEmpty instance() {
        return instance;
    }

    private BufferEmpty() {}

    @Override
    public int writeEnd() {
        return 0;
    }

    @Override
    public void writeEnd(final int writeEnd) {
        if (writeEnd != 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int writeEndMax() {
        return 0;
    }

    @Override
    protected void getAtUnchecked(final int offset, final byte[] destination, final int destinationOffset, final int length) {
        throw new IllegalStateException();
    }

    @Override
    protected void getAtUnchecked(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {
        throw new IllegalStateException();
    }

    @Override
    protected void getAtUnchecked(final int offset, final ByteBuffer destination) {
        throw new IllegalStateException();
    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        throw new IllegalStateException();
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        throw new IllegalStateException();
    }

    @Override
    protected int getS32AtUnchecked(final int offset) {
        throw new IllegalStateException();
    }

    @Override
    protected long getS64AtUnchecked(final int offset) {
        throw new IllegalStateException();
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, final int sourceOffset, final int length) {
        throw new IllegalStateException();
    }

    @Override
    protected void setAtUnchecked(final int offset, final BufferReader source, final int sourceOffset, final int length) {
        throw new IllegalStateException();
    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {
        throw new IllegalStateException();
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte value, final int length) {
        throw new IllegalStateException();
    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {
        throw new IllegalStateException();
    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {
        throw new IllegalStateException();
    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {
        throw new IllegalStateException();
    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {
        throw new IllegalStateException();
    }

    @Override
    protected void onClose() {
        // Does nothing.
    }
}
