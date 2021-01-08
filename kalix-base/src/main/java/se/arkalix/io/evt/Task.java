package se.arkalix.io.evt;

import se.arkalix.util.concurrent.Future;
import se.arkalix.util.function.ThrowingSupplier;

public interface Task<V> extends Future<V> {
    class Options<V> {
        public Options<V> action(final ThrowingSupplier<V> action) {
            // TODO: Implement.
            return this;
        }

        public Options<V> isBlocking(final boolean isBlocking) {
            // TODO: Implement.
            return this;
        }

        public Options<V> isLongLived(final boolean isLongLived) {
            // TODO: Implement.
            return this;
        }

        // TODO: Execution at scheduler shutdown must be configurable.

        public Task<V> schedule() {
            return EventLoop.main().schedule(this);
        }
    }
}
