package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

/**
 * Low-level input/output scheduler.
 */
public interface EventLoop {
    static EventLoop main() {
        throw new IllegalStateException();
    }

    Future<TcpSocket> connect(TcpSocket.Options options);

    Future<UdpSocket> connect(UdpSocket.Options options);

    Future<TcpListener> listen(TcpListener.Options options);

    Future<UdpListener> listen(UdpListener.Options options);

    Future<File> open(File.Options options);

    Future<Task> schedule(Task.Options task);

    Future<?> shutdown();
}
