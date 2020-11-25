package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface UdpListener extends IpListener<UdpSocket> {
    @Override
    default InetAddress localAddress() {
        return localSocketAddress().getAddress();
    }

    default int localPort() {
        return localSocketAddress().getPort();
    }

    InetSocketAddress localSocketAddress();

    class Options {
        public Future<UdpListener> listen() {
            return EventLoop.main().listen(this);
        }
    }
}
