package io.bigmap.store.infrastructure;

import io.bigmap.store.StoreMap;

import java.util.Optional;

public class FileMap implements StoreMap {

    @Override
    public Optional<String> get(String key) {
        return Optional.of("agentSmith");
    }

    @Override
    public void put(String key, String value) {

    }
}
