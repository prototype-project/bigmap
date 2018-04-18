package io.bigmap.store.map.application;

import io.bigmap.store.map.FileMap;
import io.bigmap.store.map.Key;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
class MapController {
    private final FileMap map;

    MapController(FileMap map) {
        this.map = map;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    String get(@PathVariable String id) {
        return map.getHead(id).orElse(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void add(@PathVariable String id, @PathVariable String version, @RequestBody String value) {
        map.add(Key.of(version, id), value);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    void delete(@PathVariable String id) {
        map.delete(id);
    }
}
