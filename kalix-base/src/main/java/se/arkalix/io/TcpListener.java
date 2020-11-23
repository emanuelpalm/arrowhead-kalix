package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

import java.util.concurrent.Flow;

public class TcpListener implements Listener<TcpSocket> {
    @Override
    public Future<?> close() {
        return null;
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super TcpSocket> subscriber) {

    }
}
