package io.bigmap.store.application;

import io.bigmap.store.domain.StoreMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = {"/map"})
class GetController {
    private final StoreMap storeMap;
    private final ApplicationMetrics applicationMetrics;

    GetController(
            StoreMap storeMap,
            ApplicationMetrics applicationMetrics) {
        this.storeMap = storeMap;
        this.applicationMetrics = applicationMetrics;
    }

    @GetMapping(path = {"{key}"})
    @ResponseStatus(value = HttpStatus.OK)
    String get(@PathVariable String key) {
        return applicationMetrics.mapGetTimer()
                .record(() -> {
                    String result = storeMap.get(key).get();
                    applicationMetrics.getMethodOutputBytesCounter().increment(result.getBytes().length);
                    return result;
                });
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Key not found"),
                HttpStatus.NOT_FOUND);
    }
}
