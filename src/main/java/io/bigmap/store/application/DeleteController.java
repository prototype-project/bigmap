package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.Role;
import io.bigmap.store.domain.StoreMap;
import io.bigmap.store.domain.StoreSetup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = {"/"})
class DeleteController {

    private final StoreMap storeMap;
    private final ReplicaNotifier replicaNotifier;
    private final StoreSetup storeSetup;

    DeleteController(
            StoreMap storeMap,
            ReplicaNotifier replicaNotifier,
            StoreSetup storeSetup) {
        this.storeMap = storeMap;
        this.replicaNotifier = replicaNotifier;
        this.storeSetup = storeSetup;
    }

    @DeleteMapping(path = {"{key}"})
    void delete(@PathVariable String key) {
        if (storeSetup.getRole().equals(Role.REPLICA)) {
            throw new ProhibitedOperationException();
        }

        storeMap.delete(key);
        replicaNotifier.notifyReplicasOnDelete(key);
    }

    @ExceptionHandler(ProhibitedOperationException.class)
    public ResponseEntity<ResponseDetails> handleReplicaPut() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Cant delete from replica."),
                HttpStatus.BAD_REQUEST);
    }
}