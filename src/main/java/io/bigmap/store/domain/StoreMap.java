package io.bigmap.store.domain;

import java.util.Optional;

public interface StoreMap {
    Optional<String> get(String key) throws CriticalError;

    void put(String key, String value) throws CriticalError;

    void delete(String key) throws CriticalError;
}
