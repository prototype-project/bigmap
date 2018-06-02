package io.bigmap.store

import io.bigmap.BaseIntegrationSpec
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class StoreBasicOperationsSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    def "should add value to store and return value from store"() {
        given:
        String key1 = UUID.randomUUID()
        String value1 = UUID.randomUUID()

        and:
        String key2 = UUID.randomUUID()
        String value2 = UUID.randomUUID()

        and:
        String key3 = UUID.randomUUID()
        String value3 = UUID.randomUUID()

        when:
        restTemplate.put(localUrl("/map/${key1}"), value1)

        then:
        restTemplate.getForEntity(localUrl("/map/${key1}"), String.class).body == value1

        when:
        restTemplate.put(localUrl("/map/${key2}"), value2)

        then:
        restTemplate.getForEntity(localUrl("/map/${key2}"), String.class).body == value2

        when:
        restTemplate.put(localUrl("/map/${key3}"), value3)

        then:
        restTemplate.getForEntity(localUrl("/map/${key3}"), String.class).body == value3
    }

    def "should override existing value"() {
        given:
        String key1 = UUID.randomUUID()

        and:
        restTemplate.put(localUrl("/map/${key1}"), "oldValue")

        when:
        restTemplate.put(localUrl("/map/${key1}"), "newValue")

        then:
        restTemplate.getForEntity(localUrl("/map/${key1}"), String.class).body == "newValue"
    }

    def "should return 404 when key not found"() {
        given:
        String key1 = UUID.randomUUID()

        when:
        restTemplate.getForEntity(localUrl("/map/${key1}"), String.class)

        then:
        thrown(HttpClientErrorException)
    }

    def "should delete key from store if exists"() {
        given:
        String key1 = UUID.randomUUID()

        and:
        restTemplate.put(localUrl("/map/${key1}"), "value")

        when:
        restTemplate.delete(localUrl("/map/${key1}"))

        and:
        restTemplate.getForEntity(localUrl("/map/${key1}"), String.class)

        then:
        thrown(HttpClientErrorException)
    }

    def "should return 400 NULL_VALUE when value not provided"() {
        given:
        String key = UUID.randomUUID()

        when:
        restTemplate.put(localUrl("/map/${key}"), null)

        then:
        thrown(HttpClientErrorException)
    }
}