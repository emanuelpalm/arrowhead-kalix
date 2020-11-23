package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

/**
 * Low-level input/output scheduler.
 */
public interface Scheduler {
    static Scheduler global() {
        throw new IllegalStateException();
    }

    Future<TcpListener> bind(TcpListener.Options options);

    Future<UdpListener> bind(UdpListener.Options options);

    Future<TcpSocket> connect(TcpSocket.Options options);

    Future<UdpSocket> connect(UdpSocket.Options options);
}
