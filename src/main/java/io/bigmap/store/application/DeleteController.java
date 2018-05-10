package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.StoreMap;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = {"/map"})
class DeleteController {

    private final StoreMap storeMap;
    private final ReplicaNotifier replicaNotifier;

    DeleteController(
            StoreMap storeMap,
            ReplicaNotifier replicaNotifier) {
        this.storeMap = storeMap;
        this.replicaNotifier = replicaNotifier;
    }

    @DeleteMapping(path = {"{key}"})
    void delete(@PathVariable String key) {
        storeMap.delete(key);
        replicaNotifier.notifyReplicasOnDelete(key);
    }

}