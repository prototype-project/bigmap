package io.bigmap.router;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = {"/router"}, produces = {APPLICATION_JSON_UTF8_VALUE})
public class RouterController {

    private final RouterSetupRepository routerSetupRepository;
    private final Router router;

    RouterController(
            RouterSetupRepository routerSetupRepository,
            Router router) {
        this.routerSetupRepository = routerSetupRepository;
        this.router = router;
    }

    @PutMapping(path = {"admin/config"})
    @ResponseStatus(value = HttpStatus.OK)
    void setupRouter(@RequestBody List<String> masterUrls) {
        this.routerSetupRepository.update(
                masterUrls.stream()
                    .map(MasterMeta::new)
                    .collect(Collectors.toList()));
    }

    @GetMapping(path = {"admin/config"})
    @ResponseStatus(value = HttpStatus.OK)
    List<MasterSetupDto> getRouterSetup() {
        return MasterSetupDto.of(routerSetupRepository.get());
    }

    @PutMapping(path = {"{key}"})
    @ResponseStatus(value = HttpStatus.OK)
    void routePut(
            @PathVariable String key,
            @RequestBody String body) {
        router.routePut(key, body);
    }

    @GetMapping(path = {"{key}"})
    @ResponseStatus(value = HttpStatus.OK)
    String routeGet(@PathVariable String key) {
        return router.routeGet(key);
    }

    @DeleteMapping(path = {"{key}"})
    @ResponseStatus(value = HttpStatus.OK)
    void routeDelete(@PathVariable String key) {
        router.routeDelete(key);
    }
}
