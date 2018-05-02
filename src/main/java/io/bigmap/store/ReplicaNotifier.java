package io.bigmap.store;

public interface ReplicaNotifier {

    void notifyReplicasOnPut(String key, String value);

    void notifyReplicasOnDelete(String key);
}
