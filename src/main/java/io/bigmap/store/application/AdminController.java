package io.bigmap.store.application;

import io.bigmap.store.domain.Role;
import io.bigmap.store.domain.StoreSetup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/admin"})
class AdminController {

    private final StoreSetup storeSetup;

    AdminController(StoreSetup storeSetup) {
        this.storeSetup = storeSetup;
    }

    @PutMapping(path = {"/set-as-master"})
    @ResponseStatus(value = HttpStatus.OK)
    void setAsMaster(@RequestBody List<String> replicas) {
        if (replicas == null) {
            throw new NullValueException();
        }
        storeSetup.setAsMaster(replicas);
    }

    @PutMapping(path = {"/set-as-replica"})
    @ResponseStatus(value = HttpStatus.OK)
    void setAsReplica() {
        storeSetup.setAsReplica();
    }

    @GetMapping(path = {"/config"})
    @ResponseStatus(value = HttpStatus.OK)
    Object currentSetup() {
        if (storeSetup.getRole().equals(Role.MASTER)) {
            return new StoreMasterSetupDto(Role.MASTER, storeSetup.getReplicas());
        } else {
            return new StoreReplicaSetupDto(Role.REPLICA);
        }
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }
}