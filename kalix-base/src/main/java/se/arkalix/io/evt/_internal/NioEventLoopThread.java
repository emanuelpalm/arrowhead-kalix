package se.arkalix.io.evt._internal;

import org.jctools.queues.MpscUnboundedXaddArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.arkalix.io.IoException;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.function.ThrowingConsumer;

import java.io.IOException;
import java.nio.channels.Selector;

@Internal
public class NioEventLoopThread extends Thread {
    private static final int EVENT_QUEUE_CHUNK_SIZE = 1024;

    private static final Logger logger = LoggerFactory.getLogger(NioEventLoopThread.class);

    private final MpscUnboundedXaddArrayQueue<ThrowingConsumer<Selector>> eventQueue = new MpscUnboundedXaddArrayQueue<>(EVENT_QUEUE_CHUNK_SIZE);
    private final Selector selector = Selector.open();

    public NioEventLoopThread(final String name) throws IOException {
        super(name);
        setUncaughtExceptionHandler((thread, throwable) ->
            logger.error("uncaught exception in thread " + thread.getName() +
                "; shutting down", throwable));
    }

    @Override
    public void run() {
        ThrowingConsumer<Selector> next;
        while (selector.isOpen()) {
            try {
                if (selector.select() > 0) {
                    for (final var selectedKey : selector.selectedKeys()) {
                        ((Runnable) selectedKey.attachment()).run();
                    }
                }

                while ((next = eventQueue.relaxedPoll()) != null) {
                    next.accept(selector);
                }
            }
            catch (final Throwable throwable) {
                logger.error("uncaught exception in thread " + getName() +
                    "; continuing", throwable);
            }
        }
        logger.debug("thread " + getName() + " shut down");
    }

    public void enqueue(final ThrowingConsumer<Selector> event) {
        eventQueue.add(event);
        selector.wakeup();
    }

    public void shutdown() {
        try {
            selector.close();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }
}
