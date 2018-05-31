package io.bigmap.router;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = {"/router"}, produces = {APPLICATION_JSON_UTF8_VALUE})
public class RouterController {

    private final RouterSetupRepository routerSetupRepository;

    RouterController(RouterSetupRepository routerSetupRepository) {
        this.routerSetupRepository = routerSetupRepository;
    }

    @PutMapping(path = {"admin/config"})
    @ResponseStatus(value = HttpStatus.OK)
    void setupRouter(@RequestBody List<String> masterUrls) {
        this.routerSetupRepository.update(
                masterUrls.stream()
                    .map(MasterMeta::new)
                    .collect(Collectors.toList())
        );
    }

    @GetMapping(path = {"admin/config"})
    ResponseEntity<List<MasterSetupDto>> getRouterSetup() {
        return new ResponseEntity<>(MasterSetupDto.of(routerSetupRepository.get()), HttpStatus.OK);
    }

}
