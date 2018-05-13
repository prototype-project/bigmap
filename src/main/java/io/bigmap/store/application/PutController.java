package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.Role;
import io.bigmap.store.domain.StoreMap;
import io.bigmap.store.domain.StoreSetup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/map"})
class PutController {

    private final StoreMap storeMap;
    private final ReplicaNotifier replicaNotifier;
    private final StoreSetup storeSetup;
    private final ApplicationMetrics applicationMetrics;

    PutController(
            StoreMap storeMap,
            ReplicaNotifier replicaNotifier,
            StoreSetup storeSetup,
            ApplicationMetrics applicationMetrics) {
        this.storeMap = storeMap;
        this.replicaNotifier = replicaNotifier;
        this.storeSetup = storeSetup;
        this.applicationMetrics = applicationMetrics;
    }

    @PutMapping(path = {"{key}"})
    void put(@PathVariable String key, @RequestBody String value) {
        applicationMetrics.mapPutTimer().record(() -> {
            if (value == null) {
                throw new NullValueException();
            }

            applicationMetrics.putMethodInputBytesCounter().increment(value.getBytes().length);

            storeMap.put(key, value);

            if (storeSetup.getRole().equals(Role.MASTER)) {
                replicaNotifier.notifyReplicasOnPut(key, value);
            }
        });
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(2, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }
}
