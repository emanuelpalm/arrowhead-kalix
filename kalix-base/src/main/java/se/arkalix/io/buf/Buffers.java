package se.arkalix.io.buf;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

@SuppressWarnings("unused")
public final class Buffers {
    private Buffers() {}

    public static ByteChannel intoByteChannel(final Buffer buffer) {
        return new ByteChannel() {
            @Override
            public int read(final ByteBuffer dst) {
                final var numberOfReadBytes = dst.remaining();
                buffer.read(dst);
                return numberOfReadBytes;
            }

            @Override
            public int write(final ByteBuffer src) {
                final var numberOfWrittenBytes = src.remaining();
                buffer.write(src);
                return numberOfWrittenBytes;
            }

            @Override
            public boolean isOpen() {
                return !buffer.isClosed();
            }

            @Override
            public void close() {
                buffer.close();
            }
        };
    }

    public static ReadableByteChannel intoReadableByteChannel(final BufferReader bufferReader) {
        return new ReadableByteChannel() {
            @Override
            public int read(final ByteBuffer dst) {
                final var numberOfReadBytes = dst.remaining();
                bufferReader.read(dst);
                return numberOfReadBytes;
            }

            @Override
            public boolean isOpen() {
                return !bufferReader.isClosed();
            }

            @Override
            public void close() {
                bufferReader.close();
            }
        };
    }

    public static WritableByteChannel intoWritableByteChannel(final BufferWriter bufferWriter) {
        return new WritableByteChannel() {
            @Override
            public int write(final ByteBuffer src) {
                final var numberOfWrittenBytes = src.remaining();
                bufferWriter.write(src);
                return numberOfWrittenBytes;
            }

            @Override
            public boolean isOpen() {
                return !bufferWriter.isClosed();
            }

            @Override
            public void close() {
                bufferWriter.close();
            }
        };
    }
}
