package io.bigmap.store.application;

import io.bigmap.store.domain.StoreMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = {"/"})
class GetController {

    private final StoreMap storeMap;

    GetController(StoreMap storeMap) {
        this.storeMap = storeMap;
    }

    @GetMapping(path = {"{key}"})
    @ResponseStatus(value = HttpStatus.OK)
    String get(@PathVariable String key) {
        return storeMap.get(key).get();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Key not found"),
                HttpStatus.NOT_FOUND);
    }
}
