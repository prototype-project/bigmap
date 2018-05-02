package io.bigmap.store.application;

import io.bigmap.store.StoreSetup;
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
    void put(@RequestBody List<String> replicas) {
        if (replicas == null) {
            throw new NullValueException();
        }
        storeSetup.setAsMaster(replicas);
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }
}