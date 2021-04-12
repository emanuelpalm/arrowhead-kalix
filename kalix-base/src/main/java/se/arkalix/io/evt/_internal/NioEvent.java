package se.arkalix.io.evt._internal;

import se.arkalix.util.annotation.Internal;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@FunctionalInterface
@Internal
public interface NioEvent {
    void onEvent(SelectionKey selectionKey);
}
