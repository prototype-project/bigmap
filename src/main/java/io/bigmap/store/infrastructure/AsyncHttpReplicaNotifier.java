package io.bigmap.store.infrastructure;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.StoreSetup;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.AsyncRestTemplate;

public class AsyncHttpReplicaNotifier implements ReplicaNotifier {
    private final AsyncRestTemplate restTemplate;
    private final StoreSetup storeSetup;

    public AsyncHttpReplicaNotifier(AsyncRestTemplate restTemplate, StoreSetup storeSetup) {
        this.restTemplate = restTemplate;
        this.storeSetup = storeSetup;
    }

    @Override
    public void notifyReplicasOnPut(String key, String value) {
        storeSetup.getReplicas().forEach(r -> restTemplate.put(r + "/" + key, new HttpEntity<>(value)));
    }

    @Override
    public void notifyReplicasOnDelete(String key) {
        storeSetup.getReplicas().forEach(r -> restTemplate.delete(r + "/" + key));
    }
}