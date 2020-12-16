package se.arkalix.io.net;

import java.net.InetAddress;

public interface IpListener<S extends IpSocket> extends Listener<S> {
    InetAddress localAddress();
}
