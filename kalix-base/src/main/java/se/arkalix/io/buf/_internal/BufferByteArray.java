package se.arkalix.io.buf._internal;

import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.buf.BufferWriter;

import java.nio.ByteBuffer;

public class BufferByteArray extends BufferBase {
    @Override
    public int readableBytesFrom(final int readOffset) {
        return 0;
    }

    @Override
    public int writeEnd() {
        return 0;
    }

    @Override
    public void writeEnd(final int writeEnd) {

    }

    @Override
    public int writeEndMax() {
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    protected void getAtUnchecked(final int offset, final byte[] destination, final int destinationOffset, final int length) {

    }

    @Override
    protected void getAtUnchecked(final int offset, final BufferWriter destination, final int destinationOffset, final int length) {

    }

    @Override
    protected void getAtUnchecked(final int offset, final ByteBuffer destination) {

    }

    @Override
    protected byte getS8AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected short getS16AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected short getS32AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected short getS64AtUnchecked(final int offset) {
        return 0;
    }

    @Override
    protected void setAtUnchecked(final int offset, final byte[] source, final int sourceOffset, final int length) {

    }

    @Override
    protected void setAtUnchecked(final int offset, final BufferReader source, final int sourceOffset, final int length) {

    }

    @Override
    protected void setAtUnchecked(final int offset, final ByteBuffer source) {

    }

    @Override
    protected void setAtUnchecked(final int offset, final byte value, final int length) {

    }

    @Override
    protected void setS8AtUnchecked(final int offset, final byte value) {

    }

    @Override
    protected void setS16AtUnchecked(final int offset, final short value) {

    }

    @Override
    protected void setS32AtUnchecked(final int offset, final int value) {

    }

    @Override
    protected void setS64AtUnchecked(final int offset, final long value) {

    }

    @Override
    public int compareTo(final BufferReader reader) {
        return 0;
    }
}
