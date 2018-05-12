package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.StoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = {"/map"})
class DeleteController {

    private static final Logger log = LoggerFactory.getLogger(DeleteController.class);

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
        log.info("DELETE key: " + key);
        storeMap.delete(key);
        replicaNotifier.notifyReplicasOnDelete(key);
    }

}