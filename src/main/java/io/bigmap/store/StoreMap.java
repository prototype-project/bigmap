package io.bigmap.store;

import java.util.Optional;

public interface StoreMap {
    Optional<String> get(String key);

    void put(String key, String value);
}
