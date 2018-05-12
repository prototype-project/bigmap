package io.bigmap.store.application;

import io.bigmap.store.domain.StoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = {"/map"})
class GetController {
    private static final Logger log = LoggerFactory.getLogger(GetController.class);

    private final StoreMap storeMap;

    GetController(
            StoreMap storeMap) {
        this.storeMap = storeMap;
    }

    @GetMapping(path = {"{key}"})
    @ResponseStatus(value = HttpStatus.OK)
    String get(@PathVariable String key) {
        log.info("GET key: " + key);
        return storeMap.get(key).get();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Key not found"),
                HttpStatus.NOT_FOUND);
    }
}
