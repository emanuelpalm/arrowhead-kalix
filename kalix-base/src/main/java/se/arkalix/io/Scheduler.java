package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.net.InetSocketAddress;

public interface Scheduler {
    Future<TcpSocket> connectTcp(InetSocketAddress socketAddress);
    Future<TcpListener> bindTcp(InetSocketAddress socketAddress);
    Future<UdpSocket> connectUdp(InetSocketAddress socketAddress);
    Future<UdpListener> bindUdp(InetSocketAddress socketAddress);
}
