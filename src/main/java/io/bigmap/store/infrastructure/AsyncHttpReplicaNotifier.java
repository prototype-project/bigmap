package io.bigmap.store.infrastructure;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.StoreSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.AsyncRestTemplate;

public class AsyncHttpReplicaNotifier implements ReplicaNotifier {

    private static final Logger log = LoggerFactory.getLogger(AsyncHttpReplicaNotifier.class);

    private final AsyncRestTemplate restTemplate;
    private final StoreSetup storeSetup;

    public AsyncHttpReplicaNotifier(AsyncRestTemplate restTemplate, StoreSetup storeSetup) {
        this.restTemplate = restTemplate;
        this.storeSetup = storeSetup;
    }

    @Override
    public void notifyReplicasOnPut(String key, String value) {
        storeSetup.getReplicas()
                .forEach(r ->
                        restTemplate.put(r + "/map/" + key, new HttpEntity<>(value))
                                .addCallback(
                                        result -> {
                                            log.info("PUT REPLICATED key: " + key + " value: " + value);
                                        },
                                        ex -> {
                                            log.warn("PUT REPLICATION FAILED key: " + key + " value: " + value);
                                        })
                );
    }

    @Override
    public void notifyReplicasOnDelete(String key) {
        storeSetup.getReplicas().forEach(r ->
                restTemplate.delete(r + "/" + key).addCallback(result -> {
                            log.info("DELETE REPLICATED key: " + key);
                        },
                        ex -> {
                            log.warn("DELETE REPLICATION FAILED key: " + key);
                        }));
    }
}