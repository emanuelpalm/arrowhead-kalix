package se.arkalix.util.concurrent;

@FunctionalInterface
public interface Subscription {
    void cancel();
}
