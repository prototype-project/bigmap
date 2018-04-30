package io.bigmap.store.application;

import io.bigmap.store.StoreMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/"})
class PutController {

    private final StoreMap storeMap;

    PutController(StoreMap storeMap) {
        this.storeMap = storeMap;
    }

    @PutMapping(path = {"{key}"})
    void put(@PathVariable String key, @RequestBody String value) {
        if (value == null) {
            throw new NullValueException();
        }
        storeMap.put(key, value);
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(2, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }
}
