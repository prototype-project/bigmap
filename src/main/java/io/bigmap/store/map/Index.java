package io.bigmap.store.map;

import io.bigmap.store.map.infrastructure.ValuePosition;

import java.util.Optional;

public interface Index {
    Optional<ValuePosition> position(Key key);

    Optional<ValuePosition> headPosition(String id);

    int tail();

    void extend(Key key, int newValueLength);

    boolean contains(Key key);

    boolean containsHead(String id);

    void delete(String id);
}
