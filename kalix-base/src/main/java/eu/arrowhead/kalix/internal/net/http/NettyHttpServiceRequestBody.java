package eu.arrowhead.kalix.internal.net.http;

import eu.arrowhead.kalix.descriptor.EncodingDescriptor;
import eu.arrowhead.kalix.dto.DataReadable;
import eu.arrowhead.kalix.net.http.service.HttpServiceRequestBody;
import eu.arrowhead.kalix.util.Result;
import eu.arrowhead.kalix.util.concurrent.Future;
import eu.arrowhead.kalix.util.concurrent.FutureProgress;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.CompositeByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NettyHttpServiceRequestBody implements HttpServiceRequestBody {
    private final ByteBufAllocator alloc;
    private final EncodingDescriptor encoding;
    private final HttpHeaders headers;

    private FutureBody<?> body;
    private Queue<HttpContent> pendingContent;
    private Throwable pendingThrowable;

    private boolean isAborted = false;
    private boolean isBodyRequested = false;
    private boolean isFinished = false;

    public NettyHttpServiceRequestBody(
        final ByteBufAllocator alloc,
        final EncodingDescriptor encoding,
        final HttpHeaders headers)
    {
        this.alloc = alloc;
        this.encoding = encoding;
        this.headers = headers;
    }

    public void abort(final Throwable throwable) {
        if (isAborted) {
            throw new IllegalStateException("Already aborted", throwable);
        }
        if (isFinished) {
            throw new IllegalStateException("Cannot abort; body finished", throwable);
        }
        isAborted = true;

        if (isBodyRequested) {
            body.abort(throwable);
        }
        else {
            pendingThrowable = throwable;
        }
    }

    public void append(final HttpContent content) {
        if (isAborted) {
            throw new IllegalStateException("Cannot append; body aborted");
        }
        if (isFinished) {
            throw new IllegalStateException("Cannot append; body finished");
        }

        // TODO: Ensure body size does not exceed some configured limit.

        if (!isBodyRequested) {
            if (pendingContent == null) {
                pendingContent = new PriorityQueue<>();
            }
            pendingContent.add(content);
            return;
        }

        if (body.isCancelled()) {
            return;
        }

        if (pendingContent != null) {
            for (final var content0 : pendingContent) {
                body.append(content0);
            }
            pendingContent = null;
        }

        body.append(content);
    }

    public void finish(final LastHttpContent lastContent) {
        if (isAborted) {
            throw new IllegalStateException("Cannot finish; body aborted");
        }
        if (isFinished) {
            throw new IllegalStateException("Already finished");
        }
        isFinished = true;

        // `headers` is the same map of headers already passed on in a
        // `HttpServiceRequest` to some `HttpService`. By adding the trailing
        // headers to the `headers` map here, they are being made visible via
        // the same `HttpServiceRequest`.
        headers.add(lastContent.trailingHeaders());

        if (isBodyRequested) {
            body.finish();
        }
    }

    @Override
    public <R extends DataReadable> FutureProgress<? extends R> bodyAs(final Class<R> class_) {
        return handleBodyRequest(() -> {
            throw new IllegalStateException("Unexpected class \"" + class_ +
                "\"; only generated DTO classes may be requested as " +
                "response bodies");
        });
    }

    @Override
    public FutureProgress<byte[]> bodyAsByteArray() {
        return handleBodyRequest(() -> new FutureBodyAsByteArray(alloc, headers));
    }

    @Override
    public <R extends DataReadable> FutureProgress<List<? extends R>> bodyAsListOf(final Class<R> class_) {
        return handleBodyRequest(() -> {
            throw new IllegalStateException("Unexpected class \"" + class_ +
                "\"; only generated DTO classes may be requested as " +
                "response bodies");
        });
    }

    @Override
    public FutureProgress<? extends InputStream> bodyAsStream() {
        return handleBodyRequest(() -> new FutureBodyAsStream(alloc, headers));
    }

    @Override
    public FutureProgress<String> bodyAsString() {
        return handleBodyRequest(() -> new FutureBodyAsString(alloc, headers));
    }

    @Override
    public FutureProgress<Path> bodyTo(final Path path, final boolean append) {
        return handleBodyRequest(() -> new FutureBodyToPath(path, append, headers));
    }

    private <V> FutureProgress<V> handleBodyRequest(final Supplier<FutureBody<V>> futureBodySupplier) {
        if (isBodyRequested) {
            throw new IllegalStateException("HTTP request body has already " +
                "been requested");
        }
        isBodyRequested = true;

        if (isAborted) {
            return FutureProgress.failure(pendingThrowable);
        }

        final var body = futureBodySupplier.get();
        this.body = body;

        if (isFinished) {
            if (pendingContent != null) {
                for (final var content : pendingContent) {
                    body.append(content);
                }
                pendingContent = null;
            }
            body.finish();
        }

        return body;
    }

    private static abstract class FutureBody<V> implements FutureProgress<V> {
        private final int expectedContentLength;

        private Consumer<Result<V>> consumer = null;
        private Result<V> pendingResult = null;
        private Listener listener = null;
        private boolean isCancelled = false;
        private boolean isCompleted = false;
        private int currentProgress = 0;

        protected FutureBody(final HttpHeaders headers) {
            this.expectedContentLength = headers.getInt("content-length", 0);
        }

        public void abort(final Throwable throwable) {
            complete(Result.failure(throwable));
        }

        public void append(final HttpContent content) {
            final var buffer = content.content();
            if (listener != null) {
                currentProgress += buffer.readableBytes();
                try {
                    listener.onProgress(currentProgress, Math.max(currentProgress, expectedContentLength));
                }
                catch (final Throwable throwable) {
                    complete(Result.failure(throwable));
                }
            }
            append(buffer);
        }

        protected abstract void append(ByteBuf buffer);

        protected void complete(final Result<V> result) {
            if (isCompleted) {
                return;
            }
            isCompleted = true;

            if (consumer != null) {
                consumer.accept(isCancelled
                    ? Result.failure(new CancellationException())
                    : result);
            }
            else {
                pendingResult = result;
            }
        }

        public abstract void finish();

        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public Future<V> onProgress(final Listener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public void onResult(final Consumer<Result<V>> consumer) {
            if (pendingResult != null) {
                consumer.accept(pendingResult);
            }
            else {
                this.consumer = consumer;
            }
        }

        @Override
        public void cancel(final boolean mayInterruptIfRunning) {
            isCancelled = true;
        }
    }

    private static abstract class FutureBodyBuffered<V> extends FutureBody<V> {
        private final CompositeByteBuf buffer;

        private FutureBodyBuffered(final ByteBufAllocator alloc, final HttpHeaders headers) {
            super(headers);
            buffer = alloc.compositeBuffer();
        }

        public abstract V assembleValue(ByteBuf buffer);

        @Override
        public void append(final ByteBuf buffer) {
            this.buffer.addComponent(buffer);
        }

        @Override
        public void finish() {
            complete(Result.success(assembleValue(buffer)));
        }
    }

    private static class FutureBodyAs<V> extends FutureBodyBuffered<V> {
        private FutureBodyAs(final ByteBufAllocator alloc, final HttpHeaders headers) {
            super(alloc, headers);
        }

        @Override
        public V assembleValue(final ByteBuf buffer) {
            buffer.release();
            return null;
        }
    }

    private static class FutureBodyAsByteArray extends FutureBodyBuffered<byte[]> {
        public FutureBodyAsByteArray(final ByteBufAllocator alloc, final HttpHeaders headers) {
            super(alloc, headers);
        }

        @Override
        public byte[] assembleValue(final ByteBuf buffer) {
            final var byteArray = new byte[buffer.readableBytes()];
            buffer.readBytes(byteArray);
            buffer.release();
            return byteArray;
        }
    }

    private static class FutureBodyToPath extends FutureBody<Path> {
        private final Path path;

        private FileOutputStream stream;

        public FutureBodyToPath(final Path path, final boolean append, final HttpHeaders headers) {
            super(headers);
            FileOutputStream stream;
            try {
                stream = new FileOutputStream(path.toFile(), append);
            }
            catch (final Throwable throwable) {
                abort(throwable);
                stream = null;
            }
            this.path = path;
            this.stream = stream;
        }

        @Override
        public void append(final ByteBuf buffer) {
            if (stream == null) {
                return;
            }
            try {
                final var length = buffer.readableBytes();
                buffer.readBytes(stream, length);
            }
            catch (final Throwable throwable) {
                abort(throwable);
            }
        }

        @Override
        public void finish() {
            Result<Path> result;
            if (stream != null) {
                try {
                    stream.close();
                    result = Result.success(path);
                }
                catch (final Throwable throwable) {
                    result = Result.failure(throwable);
                }
                complete(result);
            }
            // If stream is null, we have already presented a Throwable to the
            // consumer of this Future.
        }
    }

    private static class FutureBodyAsStream extends FutureBodyBuffered<InputStream> {
        private FutureBodyAsStream(final ByteBufAllocator alloc, final HttpHeaders headers) {
            super(alloc, headers);
        }

        @Override
        public InputStream assembleValue(final ByteBuf buffer) {
            return new ByteBufInputStream(buffer, true);
        }
    }

    private static class FutureBodyAsString extends FutureBodyBuffered<String> {
        private final Charset charset;

        public FutureBodyAsString(final ByteBufAllocator alloc, final HttpHeaders headers) {
            super(alloc, headers);
            charset = HttpUtil.getCharset(headers.get("content-type"), StandardCharsets.UTF_8);
        }

        @Override
        public String assembleValue(final ByteBuf buffer) {
            final var byteArray = new byte[buffer.readableBytes()];
            buffer.readBytes(byteArray);
            buffer.release();
            return new String(byteArray, charset);
        }
    }
}