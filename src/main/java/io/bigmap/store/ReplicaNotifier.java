package io.bigmap.store;

public interface ReplicaNotifier {
    void notifyReplicas(String key, String value);
}
