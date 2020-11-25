package se.arkalix.io._internal;

import se.arkalix.io.*;
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
