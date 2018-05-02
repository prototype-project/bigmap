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
class PutController {

    private final StoreMap storeMap;
    private final ReplicaNotifier replicaNotifier;
    private final StoreSetup storeSetup;

    PutController(
            StoreMap storeMap,
            ReplicaNotifier replicaNotifier,
            StoreSetup storeSetup) {
        this.storeMap = storeMap;
        this.replicaNotifier = replicaNotifier;
        this.storeSetup = storeSetup;
    }

    @PutMapping(path = {"{key}"})
    void put(@PathVariable String key, @RequestBody String value) {
        if (value == null) {
            throw new NullValueException();
        }
        if (storeSetup.getRole().equals(Role.REPLICA)) {
            throw new ProhibitedOperationException();
        }

        storeMap.put(key, value);
        replicaNotifier.notifyReplicasOnPut(key, value);
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(2, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProhibitedOperationException.class)
    public ResponseEntity<ResponseDetails> handleReplicaPut() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(3, "Cant write to replica."),
                HttpStatus.BAD_REQUEST);
    }
}
