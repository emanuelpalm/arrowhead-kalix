package se.arkalix.io.net;

import se.arkalix.io.evt.EventLoop;
import se.arkalix.util.concurrent.Future;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

public interface TcpSocket extends IpSocket {
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
        private InetSocketAddress localSocketAddress = null;
        private InetSocketAddress remoteSocketAddress = null;

        public Optional<InetSocketAddress> localSocketAddress() {
            return Optional.ofNullable(localSocketAddress);
        }

        public Options localSocketAddress(final InetSocketAddress localSocketAddress) {
            this.localSocketAddress = localSocketAddress;
            return this;
        }

        public Optional<InetSocketAddress> remoteSocketAddress() {
            return Optional.ofNullable(remoteSocketAddress);
        }

        public Options remoteSocketAddress(final InetSocketAddress remoteSocketAddress) {
            this.remoteSocketAddress = remoteSocketAddress;
            return this;
        }

        public Future<TcpSocket> connect() {
            return EventLoop.main().connect(this);
        }
    }
}
