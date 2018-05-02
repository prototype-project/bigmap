package io.bigmap.store.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bigmap.store.Role;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class StoreMasterSetupDto {

    @NotEmpty
    private final Role role;

    @NotEmpty
    private final List<String> replicas;

    @JsonCreator
    StoreMasterSetupDto(@JsonProperty("role") Role role, @JsonProperty("replicas") List<String> replicas) {
        this.role = role;
        this.replicas = replicas;
    }

    public Role getRole() {
        return role;
    }

    public List<String> getReplicas() {
        return replicas;
    }
}
