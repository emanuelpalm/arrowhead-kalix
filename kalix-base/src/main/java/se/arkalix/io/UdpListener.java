package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public class UdpListener implements Listener<UdpSocket> {
    @Override
    public Future<?> close() {
        return null;
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super UdpSocket> subscriber) {

    }
}
