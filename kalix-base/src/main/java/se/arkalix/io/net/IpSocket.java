package se.arkalix.io.net;

import java.net.InetAddress;

public interface IpSocket extends Socket {
    InetAddress localAddress();

    InetAddress remoteAddress();
}
