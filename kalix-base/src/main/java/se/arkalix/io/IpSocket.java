package se.arkalix.io;

import java.net.InetAddress;

public interface IpSocket extends Socket {
    InetAddress localAddress();

    InetAddress remoteAddress();
}
