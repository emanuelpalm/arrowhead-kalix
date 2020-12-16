package se.arkalix.io.evt._internal;

import se.arkalix.io.fs.File;
import se.arkalix.io.evt.EventLoop;
import se.arkalix.io.evt.Task;
import se.arkalix.io.net.TcpListener;
import se.arkalix.io.net.TcpSocket;
import se.arkalix.io.net.UdpListener;
import se.arkalix.io.net.UdpSocket;
import se.arkalix.util.concurrent.Future;

public class NioEventLoop implements EventLoop {
    // TODO: Make size of main event loop thread pool configurable.

    @Override
    public Future<TcpSocket> connect(final TcpSocket.Options options) {
        return null;
    }

    @Override
    public Future<UdpSocket> connect(final UdpSocket.Options options) {
        return null;
    }

    @Override
    public Future<TcpListener> listen(final TcpListener.Options options) {
        return null;
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
    public Future<Task> schedule(final Task.Options task) {
        return null;
    }

    @Override
    public Future<?> shutdown() {
        return null;
    }
}
