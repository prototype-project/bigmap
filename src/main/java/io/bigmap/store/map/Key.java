package io.bigmap.store.map;

import com.google.common.base.Preconditions;

public class Key {
    private final String version;
    private final String id;

    private Key(String version, String id) {
        Preconditions.checkNotNull(version);
        Preconditions.checkNotNull(id);
        this.version = version;
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public static Key of(String version, String id) {
        return new Key(version, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (!version.equals(key.version)) return false;
        return id.equals(key.id);
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}