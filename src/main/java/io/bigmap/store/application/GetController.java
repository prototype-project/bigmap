package io.bigmap.store.application;

import io.bigmap.store.domain.StoreMap;
import org.springframework.http.HttpStatus;
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
                .record(() -> storeMap.get(key)
                        .map(result -> {
                            applicationMetrics.getMethodOutputBytesCounter().increment(result.getBytes().length);
                            return result;
                        }).orElseThrow(NoSuchElementException::new)
                );
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound() {
        return "KEY_NOT_FOUND";
    }
}
