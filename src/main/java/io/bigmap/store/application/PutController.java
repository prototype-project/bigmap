package io.bigmap.store.application;

import io.bigmap.store.domain.ReplicaNotifier;
import io.bigmap.store.domain.Role;
import io.bigmap.store.domain.StoreMap;
import io.bigmap.store.domain.StoreSetup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    @ResponseStatus(HttpStatus.OK)
    void put(@PathVariable String key, @RequestBody String value) {
        applicationMetrics.mapPutTimer().record(() ->
            Optional.ofNullable(value)
                    .ifPresentOrElse(v -> {
                        storeMap.put(key, value);
                        applicationMetrics.putMethodInputBytesCounter()
                                .increment(value.getBytes().length);
                        if (storeSetup.getRole().equals(Role.MASTER)) {
                            replicaNotifier.notifyReplicasOnPut(key, value);
                        }
                    }, () -> {
                        throw new NullValueException();
                    })
        );
    }

    @ExceptionHandler(NullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotFound() {
        return "NULL_VALUE";
    }
}
