package io.bigmap.store.map;

import io.bigmap.store.map.infrastructure.ValuePosition;

public interface Index {
    ValuePosition position(Key key);

    ValuePosition headPosition(String id);

    int tail();

    void extend(Key key, int newValueLength);

    boolean contains(Key key);


}
