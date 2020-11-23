package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface TcpListener extends IpListener<TcpSocket> {
    static Options create() {
        return new Options();
    }

    @Override
    default InetAddress localAddress() {
        return localSocketAddress().getAddress();
    }

    default int localPort() {
        return localSocketAddress().getPort();
    }

    InetSocketAddress localSocketAddress();

    class Options {
        public Future<TcpListener> bind() {
            return Scheduler.global().bind(this);
        }
    }
}
