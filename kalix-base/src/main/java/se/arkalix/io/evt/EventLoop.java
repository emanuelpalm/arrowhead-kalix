package se.arkalix.io.evt;

import se.arkalix.io.fs.File;
import se.arkalix.io.net.TcpListener;
import se.arkalix.io.net.TcpSocket;
import se.arkalix.io.net.UdpListener;
import se.arkalix.io.net.UdpSocket;
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
