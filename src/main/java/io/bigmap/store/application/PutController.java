package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.Role;
import io.bigmap.store.domain.StoreMap;
import io.bigmap.store.domain.StoreSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/map"})
class PutController {

    private static final Logger log = LoggerFactory.getLogger(PutController.class);

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
        log.info("PUT key: " + key + " value: " + value);

        if (value == null) {
            throw new NullValueException();
        }

        storeMap.put(key, value);

        if (storeSetup.getRole().equals(Role.MASTER)) {
            log.info("NOTIFY-PUT key: " + key + " value: " + value);
            replicaNotifier.notifyReplicasOnPut(key, value);
        }

    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(2, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }
}
