package se.arkalix.io;

import se.arkalix.util.concurrent.Future;

public interface File {
    class Options {
        public Future<File> open() {
            return EventLoop.main().open(this);
        }
    }
}
