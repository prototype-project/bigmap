package io.bigmap.router;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.bigmap.common.CriticalError;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class SyncRouterSetupRepository implements RouterSetupRepository {
    private final RestTemplate restTemplate;
    private List<MasterMeta> masters;

    SyncRouterSetupRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.masters = ImmutableList.copyOf(emptyList());
    }

    @Override
    public void update(List<MasterMeta> masters) {
        this.masters = ImmutableList.copyOf(masters);
    }

    @Override
    public RouterSetup get() {
        return Optional.of(masters.stream()
                    .map(masterMeta ->
                            restTemplate.exchange(masterMeta.getConfigUrl(), HttpMethod.GET,null, MasterSetupDto.class))
                    .map(response -> Optional.ofNullable(response.getBody())
                                        .orElseThrow(() -> new CriticalError("Could not fetch master config")))
                    .map(master -> new RouterSetup.MasterSetup(master.replicas.stream()
                                .map(ReplicaMeta::new)
                                .collect(Collectors.toList()), master.address))
                    .collect(Collectors.toList()))
                .map(RouterSetup::new)
                .orElseThrow(() -> new CriticalError("Could not fetch master config"));
    }

    private static class MasterSetupDto {
        private final String role;
        private final List<String> replicas;
        private final String address;

        @JsonCreator
        MasterSetupDto(
                @JsonProperty("role") String role,
                @JsonProperty("replicas") List<String> replicas,
                @JsonProperty("address") String address) {
            this.role = role;
            this.replicas = replicas;
            this.address = address;
        }

        public String getRole() {
            return role;
        }

        public List<String> getReplicas() {
            return replicas;
        }

        public String getAddress() {
            return address;
        }
    }
}
