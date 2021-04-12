package se.arkalix.io.net;

import se.arkalix.io.evt.EventLoop;
import se.arkalix.util.concurrent.Future;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

public interface TcpListener extends IpListener<TcpSocket> {
    @Override
    default InetAddress localAddress() {
        return localSocketAddress().getAddress();
    }

    default int localPort() {
        return localSocketAddress().getPort();
    }

    InetSocketAddress localSocketAddress();

    class Options {
        private InetSocketAddress localSocketAddress = null;

        public Optional<InetSocketAddress> localSocketAddress() {
            return Optional.ofNullable(localSocketAddress);
        }

        public Options localSocketAddress(final InetSocketAddress localSocketAddress) {
            this.localSocketAddress = localSocketAddress;
            return this;
        }
        public Future<TcpListener> listen() {
            return EventLoop.main().listen(this);
        }
    }
}
