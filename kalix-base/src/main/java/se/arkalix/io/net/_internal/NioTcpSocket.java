package se.arkalix.io.net._internal;

import se.arkalix.io.IoException;
import se.arkalix.io.buf.BufferAllocator;
import se.arkalix.io.buf.BufferReader;
import se.arkalix.io.net.SocketIsClosed;
import se.arkalix.io.net.TcpSocket;
import se.arkalix.util.annotation.Internal;
import se.arkalix.util.concurrent.Future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;

@Internal
public class NioTcpSocket implements TcpSocket {
    private final AtomicReference<SelectionKey> selectionKey = new AtomicReference<>(null);
    private final SocketChannel socketChannel;

    private Flow.Subscriber<? super BufferReader> subscriber = null;

    public NioTcpSocket(
        final BufferAllocator bufferAllocator,
        final Selector selector,
        final SocketChannel socketChannel
    ) {
        this.socketChannel = Objects.requireNonNull(socketChannel, "socketChannel");

        try {
            final var ops = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
            selectionKey.set(socketChannel.register(selector, ops, (Runnable) () -> {
                final var selectionKey0 = selectionKey.get();
                if (selectionKey0 == null) {
                    try {
                        subscriber.onError(new IllegalStateException("selectionKey == null"));
                    }
                    finally {
                        closeSocketChannel();
                    }
                    return;
                }
                try {
                    while (selectionKey0.isReadable()) {
                        final var buffer = bufferAllocator.allocate(8192, 8192);
                        socketChannel.read(buffer.toByteBuffers());
                    }
                    while (selectionKey0.isWritable()) {
                        final var buffer = bufferAllocator.allocate(8192, 8192); // TODO
                        socketChannel.write(buffer.toByteBuffers());
                    }
                }
                catch (final IOException exception) {
                    try {
                        subscriber.onError(new IoException(exception));
                    }
                    finally {
                        closeSocketChannel();
                    }
                }
            }));
        }
        catch (final ClosedChannelException exception) {
            throw new SocketIsClosed(exception);
        }
    }

    @Override
    public Future<?> write(final BufferReader buffer) {
        return null;
    }

    @Override
    public void close() {
        closeSocketChannel();
    }

    private void closeSocketChannel() {
        try {
            socketChannel.close();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }

    @Override
    public InetSocketAddress localSocketAddress() {
        try {
            return (InetSocketAddress) socketChannel.getLocalAddress();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }

    @Override
    public InetSocketAddress remoteSocketAddress() {
        try {
            return (InetSocketAddress) socketChannel.getRemoteAddress();
        }
        catch (final IOException exception) {
            throw new IoException(exception);
        }
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super BufferReader> subscriber) {
        if (this.subscriber != null) {
            throw new IllegalStateException("subscriber already set");
        }
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber");
    }
}
