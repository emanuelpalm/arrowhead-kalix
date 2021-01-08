package se.arkalix.util.concurrent;

@FunctionalInterface
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
