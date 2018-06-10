package io.bigmap.store.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.bigmap.store.domain.Role;


import javax.validation.constraints.NotEmpty;
import java.util.List;

public class StoreMasterSetupDto {

    @NotEmpty
    private final Role role;

    @NotEmpty
    private final String address;

    @NotEmpty
    private final List<String> replicas;

    @JsonCreator
    StoreMasterSetupDto(
            @JsonProperty("role") Role role,
            @JsonProperty("replicas") List<String> replicas,
            @JsonProperty("address") String address) {
        this.role = role;
        this.replicas = replicas;
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public List<String> getReplicas() {
        return replicas;
    }

    public String getAddress() {
        return address;
    }
}
