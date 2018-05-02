package io.bigmap.store

import org.springframework.web.client.RestTemplate

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Predicate

class StoreHeavySpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()
    ExecutorService executor = Executors.newFixedThreadPool(10);

    def "should handle high load"() {
        given:
        Map<String, List<String>> data = new HashMap<>()
        (1..100).stream()
                .map({i -> UUID.randomUUID().toString()})
                .forEach({String i ->
                    (1..1000).forEach({j ->
                        if (data[i] == null) {
                        data[i] = []
                    }
                    data[i].add(UUID.randomUUID().toString())
                })
        })

        when:
        List<Future<Boolean>> futures = []
        data.forEach({key, items ->
            futures.add(executor.submit(new Callable<Boolean>() {
                @Override
                Boolean call() throws Exception {
                    items.forEach({value ->
                        restTemplate.put(localUrl("/map/${key}"), value)
                    })
                    return restTemplate.getForEntity(localUrl("/map/${key}"), String.class).body == items.last()
                }
            }))
        })

        then:
        futures.stream()
                .map({f -> f.get()})
                .allMatch(Predicate.isEqual(true))
    }
}
