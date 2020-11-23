package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface UdpSocket extends IpSocket {
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

    @Override
    default InetAddress remoteAddress() {
        return remoteSocketAddress().getAddress();
    }

    default int remotePort() {
        return remoteSocketAddress().getPort();
    }

    InetSocketAddress remoteSocketAddress();

    class Options {
        public Future<UdpSocket> bind() {
            return Scheduler.global().connect(this);
        }
    }
}
