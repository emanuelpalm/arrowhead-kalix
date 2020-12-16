package se.arkalix.io.fs;

import se.arkalix.util.concurrent.Future;

public interface FileWriter {

    Future<?> flushAll();

    Future<?> flushData();
}
