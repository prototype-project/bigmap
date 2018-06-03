package io.bigmap.router;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import io.bigmap.common.CriticalError;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class HttpRouter implements Router {
    private final RestTemplate restTemplate;
    private final RouterSetupRepository routerSetup;

    HttpRouter(
            RestTemplate restTemplate,
            RouterSetupRepository routerSetup) {
        this.restTemplate = restTemplate;
        this.routerSetup = routerSetup;
    }

    @Override
    public String routeGet(String key) {
        RouterSetup.MasterSetup masterSetup = pickMaster(key);
        try {
            return restTemplate.getForEntity(pickRouter(masterSetup, key).getKeyUrl(key), String.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getResponseBodyAsString(), e, e.getStatusCode());
        }
    }

    @Override
    public void routePut(String key, String value) {
        try {
            restTemplate.put(pickMaster(key).getKeyUrl(key), value, String.class);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getResponseBodyAsString(), e, e.getStatusCode());
        }
    }

    @Override
    public void routeDelete(String key) {
        try {
            restTemplate.delete(pickMaster(key).getKeyUrl(key));
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getResponseBodyAsString(), e, e.getStatusCode());
        }
    }

    private RouterSetup.MasterSetup pickMaster(String key) {
        List<RouterSetup.MasterSetup> masters = routerSetup.get().getMasters();
        return Optional.of(HashPredicate.of(masters.size())
                .match(key))
                .map(masters::get)
                .orElseThrow(() -> new CriticalError("Could not map index to master."));
    }

    private ReplicaMeta pickRouter(RouterSetup.MasterSetup masterSetup, String key) {
        List<ReplicaMeta> replicas = masterSetup.getReplicaMetaList();
        return Optional.of(HashPredicate.of(replicas.size())
                .match(key))
                .map(replicas::get)
                .orElseThrow(() -> new CriticalError("Could not map index to replica."));
    }

    private static class PercentageRage {
        private final int index;
        private final int from;
        private final int to;

        PercentageRage(int index, int from, int to) {
            this.index = index;
            this.from = from;
            this.to = to;
        }

        boolean match(int x) {
            return from <= x && x <= to;
        }

        int getIndex() {
            return index;
        }

        static List<PercentageRage> of(int numberOfElements) {
            return IntStream.range(0, numberOfElements)
                    .mapToObj(i ->
                            new PercentageRage(i, i * 100 / numberOfElements, (i + 1) * 100 / numberOfElements))
                    .collect(Collectors.toList());
        }
    }

    private static class HashPredicate {
        private final List<PercentageRage> ranges;

        HashPredicate(List<PercentageRage> ranges) {
            this.ranges = ranges;
        }

        Function<String, Integer> hashingFunction() {
            return (originalString -> {
                HashCode h = Hashing.sha256().hashString(originalString, StandardCharsets.UTF_8);
                return Math.abs(h.asInt()) % 100;
            });
        }

        int match(String key) {
            return ranges.stream()
                    .filter(range -> range.match(hashingFunction().apply(key)))
                    .findFirst()
                    .map(PercentageRage::getIndex)
                    .orElseThrow(() -> new CriticalError("Could not find matching range for key."));
        }

        static HashPredicate of(int numberOfElements) {
            return new HashPredicate(PercentageRage.of(numberOfElements));
        }
    }
}
