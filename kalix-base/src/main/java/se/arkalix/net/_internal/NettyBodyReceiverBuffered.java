package se.arkalix.net._internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.arkalix.io._buf.old.ReadableBuffer;
import se.arkalix.io._buf.old._internal.ReadableBufferOfByteBuf;
import se.arkalix.net.BodyIncoming;
import se.arkalix.util.Result;
import se.arkalix.util.concurrent.Future;

import java.util.Objects;
import java.util.function.Consumer;

public class NettyBodyReceiverBuffered implements NettyBodyReceiver, Future<ReadableBuffer> {
    private static final Logger logger = LoggerFactory.getLogger(BodyIncoming.class);

    private final CompositeByteBuf buffer;

    private boolean isCancelled = false;
    private Consumer<Result<ReadableBuffer>> consumer;
    private Result<ReadableBuffer> result;

    public NettyBodyReceiverBuffered(final ByteBufAllocator allocator) {
        buffer = Objects.requireNonNull(allocator, "allocator").compositeBuffer();
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void abort(final Throwable cause) {
        result = Result.ofFault(cause);
        if (consumer != null) {
            final var consumer0 = consumer;
            consumer = null;
            final var result0 = result;
            result = null;
            try {
                consumer0.accept(result0);
            }
            catch (final Throwable throwable) {
                if (logger.isErrorEnabled()) {
                    throwable.addSuppressed(cause);
                    logger.error("Future result consumer unexpectedly threw " +
                        "exception after being notified about a fault", throwable);
                }
            }
        }
        buffer.release();
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        if (result == null) {
            byteBuf.retain();
            this.buffer.addComponent(true, byteBuf);
        }
    }

    @Override
    public void close() {
        if (result == null) {
            result = Result.ofValue(new ReadableBufferOfByteBuf(buffer));
        }
        if (consumer != null) {
            final var consumer0 = consumer;
            consumer = null;
            final var result0 = result;
            result = null;
            try {
                consumer0.accept(result0);
            }
            catch (final Throwable throwable) {
                abort(throwable);
            }
        }
    }

    @Override
    public void onResult(final Consumer<Result<ReadableBuffer>> consumer) {
        if (result != null) {
            final var result0 = result;
            result = null;
            try {
                consumer.accept(result0);
            }
            catch (final Throwable throwable) {
                abort(throwable);
            }
        }
        else {
            this.consumer = consumer;
        }
    }

    @Override
    public void cancel(final boolean mayInterruptIfRunning) {
        isCancelled = true;
        buffer.release();
    }
}
