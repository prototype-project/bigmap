package io.bigmap.store.domain;

public interface ReplicaNotifier {

    void notifyReplicasOnPut(String key, String value);

    void notifyReplicasOnDelete(String key);
}
