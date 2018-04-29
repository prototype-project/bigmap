package io.bigmap.store.application;

import io.bigmap.store.StoreMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/"})
class GetController {

    private final StoreMap storeMap;

    GetController(StoreMap storeMap) {
        this.storeMap = storeMap;
    }

    @GetMapping(path = {"{key}"})
    String get(@PathVariable String key) {
        return storeMap.get(key).get();
    }
}
