package se.arkalix.io;

import java.net.InetAddress;

public interface IpListener<S extends IpSocket> extends Listener<S> {
    InetAddress localAddress();
}
