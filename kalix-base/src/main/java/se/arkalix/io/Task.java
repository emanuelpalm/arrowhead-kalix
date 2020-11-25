package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

public interface Task {
    class Options {
        // TODO: Execution at scheduler shutdown must be configurable.

        public Future<Task> schedule() {
            return EventLoop.main().schedule(this);
        }
    }
}
