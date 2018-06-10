package io.bigmap.store.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bigmap.store.domain.Role;
import org.hibernate.validator.constraints.NotEmpty;

public class StoreReplicaSetupDto {

    @NotEmpty
    private final Role role;

    private final String address;

    @JsonCreator
    StoreReplicaSetupDto(
            @JsonProperty("role") Role role,
            @JsonProperty("address") String address) {
        this.role = role;
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }
}
