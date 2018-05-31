package io.bigmap.router;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class MasterSetupDto {
    private final String master;
    private final List<String> replicas;

    @JsonCreator
    public MasterSetupDto(
            @JsonProperty("master") String master,
            @JsonProperty("replicas") List<String> replicas) {
        this.master = master;
        this.replicas = replicas;
    }

    public String getMaster() {
        return master;
    }

    public List<String> getReplicas() {
        return replicas;
    }

    static List<MasterSetupDto> of(RouterSetup routerSetup) {
        return routerSetup.getMasters().stream()
                .map(masterSetup ->
                        new MasterSetupDto(
                                masterSetup.getMasterAddress(),
                                masterSetup.getReplicaMetaList().stream()
                                    .map(ReplicaMeta::getUrl)
                                    .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
