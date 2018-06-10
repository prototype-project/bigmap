package io.bigmap.store.application;

import io.bigmap.store.domain.Role;
import io.bigmap.store.domain.StoreSetup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = {"/map/admin"})
class AdminController {

    private final StoreSetup storeSetup;

    AdminController(StoreSetup storeSetup) {
        this.storeSetup = storeSetup;
    }

    @PutMapping(path = {"/set-as-master"})
    @ResponseStatus(value = HttpStatus.OK)
    void setAsMaster(@RequestBody List<String> replicas) {
        if (replicas == null) {
            throw new NullValueException();
        }
        storeSetup.setAsMaster(replicas);
    }

    @PutMapping(path = {"/set-as-replica"})
    @ResponseStatus(value = HttpStatus.OK)
    void setAsReplica() {
        storeSetup.setAsReplica();
    }

    @GetMapping(path = {"/config"})
    @ResponseStatus(value = HttpStatus.OK)
    Object currentSetup(HttpServletRequest request) {
        if (storeSetup.getRole().equals(Role.MASTER)) {
            return new StoreMasterSetupDto(Role.MASTER, storeSetup.getReplicas(), getURL(request));
        } else {
            return new StoreReplicaSetupDto(Role.REPLICA, getURL(request));
        }
    }

    private String getURL(HttpServletRequest request){
        String fullURL = request.getRequestURL().toString();
        return fullURL.substring(0, StringUtils.ordinalIndexOf(fullURL, "/", 3));
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ResponseDetails> handleNotFound() {
        return new ResponseEntity<ResponseDetails>(
                new ResponseDetails(1, "Value cant be empty."),
                HttpStatus.BAD_REQUEST);
    }
}