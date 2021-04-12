package se.arkalix.io.evt._internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.arkalix.io.IoException;
import se.arkalix.io.buf.BufferAllocator;
import se.arkalix.io.evt.EventLoop;
import se.arkalix.io.evt.Task;
import se.arkalix.io.fs.File;
import se.arkalix.io.net.TcpListener;
import se.arkalix.io.net.TcpSocket;
import se.arkalix.io.net.UdpListener;
import se.arkalix.io.net.UdpSocket;
import se.arkalix.io.net._internal.NioTcpListener;
import se.arkalix.util.Result;
import se.arkalix.util._internal.BinaryMath;
import se.arkalix.util._internal.SystemProperties;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.concurrent.Future;
import se.arkalix.util.concurrent._internal.FutureCompletion;
import se.arkalix.util.concurrent._internal.FutureCompletionUnsafe;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

@Internal
public class NioEventLoop implements EventLoop {
    static final int MAIN_THREAD_COUNT;

    private static NioEventLoop mainEventLoop = null;

    private static final Logger logger = LoggerFactory.getLogger(NioEventLoop.class);

    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private final BufferAllocator bufferAllocator = BufferAllocator.createPooledDirect();

    private final NioEventLoopThread[] threads;
    private final IntUnaryOperator threadIndexUpdater;
    private final AtomicInteger threadIndex = new AtomicInteger(0);

    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    public synchronized static NioEventLoop main() {
        if (mainEventLoop == null) {
            try {
                mainEventLoop = new NioEventLoop(MAIN_THREAD_COUNT);
            }
            catch (IOException exception) {
                throw new IoException(exception);
            }
        }
        return mainEventLoop;
    }

    public NioEventLoop(final int threadCount) throws IOException {
        threads = new NioEventLoopThread[threadCount];
        final var threadId = Thread.currentThread().getId();
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new NioEventLoopThread("kalix-nio-" + threadId + "-" + i);
        }
        if (BinaryMath.isPositivePowerOfTwo(threadCount)) {
            final var threadMask = threadCount - 1;
            threadIndexUpdater = index -> (index + 1) & threadMask;
        }
        else {
            threadIndexUpdater = index -> (index + 1) % threadCount;
        }
    }

    public NioEventLoopThread nextThread() {
        return threads[threadIndex.getAndUpdate(threadIndexUpdater)];
    }

    @Override
    public Future<TcpSocket> connect(final TcpSocket.Options options) {
        final var future = new FutureCompletionUnsafe<TcpSocket>();
        nextThread().enqueue(selector -> {
            final var socketChannel = selector.provider().openSocketChannel();
            socketChannel.configureBlocking(false);

            final var localSocketAddress = options.localSocketAddress().orElse(null);
            if (localSocketAddress != null) {
                socketChannel.bind(localSocketAddress);
            }

            socketChannel.connect(options.remoteSocketAddress()
                .orElseThrow(() -> new NullPointerException("remoteSocketAddress")));

            socketChannel.register(selector, SelectionKey.OP_CONNECT, future);
        });
        return future;
    }

    @Override
    public Future<UdpSocket> connect(final UdpSocket.Options options) {
        return null;
    }

    @Override
    public Future<TcpListener> listen(final TcpListener.Options options) {
        final var future = new FutureCompletion<TcpListener>();
        nextThread().enqueue(selector -> {
            final var serverSocketChannel = selector.provider().openServerSocketChannel();
            serverSocketChannel.configureBlocking(false);

            // TODO: serverSocketChannel.setOption(SocketOptions...)

            final var localSocketAddress = options.localSocketAddress().orElse(null);
            if (localSocketAddress == null) {
                future.complete(Result.ofFault(new NullPointerException("localSocketAddress")));
                return;
            }

            serverSocketChannel.bind(options.localSocketAddress()
                .orElseThrow(() -> new NullPointerException("localSocketAddress")));

            future.complete(Result.ofValue(new NioTcpListener(this, selector, serverSocketChannel)));
        });
        return future;
    }

    @Override
    public Future<UdpListener> listen(final UdpListener.Options options) {
        return null;
    }

    @Override
    public Future<File> open(final File.Options options) {
        return null;
    }

    @Override
    public <V> Task<V> schedule(final Task.Options<V> task) {
        return null;
    }

    @Override
    public Future<?> shutdown() {
        return null;
    }

    public BufferAllocator bufferAllocator() {
        return bufferAllocator;
    }

    static {
        MAIN_THREAD_COUNT = SystemProperties.getInteger("se.arkalix.io.evt.mainEventLoopThreadCount")
            .orElse(Runtime.getRuntime().availableProcessors());
    }
}
