package io.bigmap.store.map.application;

import io.bigmap.store.map.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
class MapController {
    private final FileMap map;

    MapController(FileMap map) {
        this.map = map;
    }

    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody String get(@PathVariable String id) {
        return map.getHead(id).orElseThrow(KeyNotFoundException::new);
    }

    @PostMapping(path = "{id}/{version}")
    @ResponseStatus(HttpStatus.CREATED)
    void add(@PathVariable String id, @PathVariable String version, @RequestBody String value) {
        map.add(Key.of(version, id), value);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    void delete(@PathVariable String id) {
        map.delete(id);
    }

    @ExceptionHandler(CriticalError.class)
    ResponseEntity handle(CriticalError exception) {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(KeyDuplicationException.class)
    ResponseEntity handle(KeyDuplicationException exception) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KeyNotFoundException.class)
    ResponseEntity handle(KeyNotFoundException exception) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
