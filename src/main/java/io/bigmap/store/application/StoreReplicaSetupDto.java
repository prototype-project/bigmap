package io.bigmap.store.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bigmap.store.domain.Role;
import org.hibernate.validator.constraints.NotEmpty;

public class StoreReplicaSetupDto {

    @NotEmpty
    private final Role role;

    @JsonCreator
    StoreReplicaSetupDto(@JsonProperty("role") Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
