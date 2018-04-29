package io.bigmap.store.application;

import io.bigmap.store.StoreMap;
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
        storeMap.put(key, value);
    }
}
