package io.bigmap.store.map.infrastructure;

import io.bigmap.store.map.Index;
import io.bigmap.store.map.Key;
import io.bigmap.store.map.ValuePosition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryIndex implements Index {
    private final Map<Key, ValuePosition> positions = new ConcurrentHashMap<>();
    private final Map<String, ValuePosition> headPositions = new ConcurrentHashMap<>();
    private int tail = 0;

    @Override
    public Optional<ValuePosition> position(Key key) {
        return Optional.ofNullable(positions.get(key));
    }

    @Override
    public Optional<ValuePosition> headPosition(String id) {
        return Optional.ofNullable(headPositions.get(id));
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

    @Override
    public boolean containsHead(String id) {
        return headPositions.containsKey(id);
    }

    @Override
    public void delete(String id) {
        headPositions.remove(id);
        List<Key> toRemove = positions.entrySet().stream()
                .filter(e -> e.getKey().getId().equals(id))
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        toRemove.forEach(positions::remove);
    }
}
