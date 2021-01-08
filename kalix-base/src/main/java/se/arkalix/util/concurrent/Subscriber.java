package se.arkalix.util.concurrent;

public interface Subscriber<T> {
    void onSubscribe(Subscription subscription);

    void onClose();

    void onFault(Throwable fault);

    void onNext(T value);
}
