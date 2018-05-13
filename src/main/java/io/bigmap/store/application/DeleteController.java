package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.StoreMap;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = {"/map"})
class DeleteController {

    private final StoreMap storeMap;
    private final ReplicaNotifier replicaNotifier;
    private final ApplicationMetrics applicationMetrics;

    DeleteController(
            StoreMap storeMap,
            ReplicaNotifier replicaNotifier,
            ApplicationMetrics applicationMetrics) {
        this.storeMap = storeMap;
        this.replicaNotifier = replicaNotifier;
        this.applicationMetrics = applicationMetrics;
    }

    @DeleteMapping(path = {"{key}"})
    void delete(@PathVariable String key) {
        applicationMetrics.mapDeleteTimer().record(() -> {
            storeMap.delete(key);
            replicaNotifier.notifyReplicasOnDelete(key);
        });
    }

}