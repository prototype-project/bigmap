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
    private final InfrastructureMetrics infrastructureMetrics;

    public AsyncHttpReplicaNotifier(
            AsyncRestTemplate restTemplate,
            StoreSetup storeSetup,
            InfrastructureMetrics infrastructureMetrics) {
        this.restTemplate = restTemplate;
        this.storeSetup = storeSetup;
        this.infrastructureMetrics = infrastructureMetrics;
    }

    @Override
    public void notifyReplicasOnPut(String key, String value) {
        storeSetup.getReplicas()
                .forEach(r -> restTemplate.put(r + "/map/" + key, new HttpEntity<>(value))
                        .addCallback(
                                result -> infrastructureMetrics.notifySuccessCounter().increment(),
                                ex -> {
                                    infrastructureMetrics.notifyFailureCounter().increment();
                                    log.error(ex.getMessage());
                                }));
    }

    @Override
    public void notifyReplicasOnDelete(String key) {
        storeSetup.getReplicas().forEach(r ->
                restTemplate.delete(r + "/map/" + key).addCallback(result ->
                                infrastructureMetrics.notifySuccessCounter().increment(),
                        ex -> {
                            infrastructureMetrics.notifyFailureCounter().increment();
                            log.error(ex.getMessage());
                        }));
    }
}