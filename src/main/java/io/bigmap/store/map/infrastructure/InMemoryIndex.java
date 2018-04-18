package io.bigmap.store.map.infrastructure;

import io.bigmap.store.map.Index;
import io.bigmap.store.map.Key;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIndex implements Index {
    private final Map<Key, ValuePosition> positions = new ConcurrentHashMap<>();
    private final Map<String, ValuePosition> headPositions = new ConcurrentHashMap<>();
    private int tail = 0;

    @Override
    public ValuePosition position(Key key) {
        return positions.get(key);
    }

    @Override
    public ValuePosition headPosition(String id) {
        return headPositions.get(id);
    }

    @Override
    public int tail() {
        return tail;
    }

    @Override
    public void extend(Key key, int length) {
        positions.put(key, new ValuePosition(tail, length));
        headPositions.put(key.getId(), new ValuePosition(tail, length));
        tail += length;
    }

    @Override
    public boolean contains(Key key) {
        return positions.containsKey(key);
    }
}
