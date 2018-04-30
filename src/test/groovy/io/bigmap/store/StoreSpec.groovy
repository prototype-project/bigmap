package io.bigmap.store

import org.springframework.web.client.RestTemplate

class StoreSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    def "should add value to store and return value from store"() {
        given:

        String key1 = 'johnSmith1'
        String value1 = 'agentSmith1'

        and:
        String key2 = 'johnSmith2'
        String value2 = 'agentSmith2'

        and:
        String key3 = 'johnSmith3'
        String value3 = 'agentSmith3'

        when:
        restTemplate.put(localUrl("/${key1}"), value1)

        then:
        restTemplate.getForEntity(localUrl("/${key1}"), String.class).body == value1

        when:
        restTemplate.put(localUrl("/${key2}"), value2)

        then:
        restTemplate.getForEntity(localUrl("/${key2}"), String.class).body == value2

        when:
        restTemplate.put(localUrl("/${key3}"), value3)

        then:
        restTemplate.getForEntity(localUrl("/${key3}"), String.class).body == value3
    }

    def "should override existing value"() {
        given:
        String key1 = 'johnSmith1'

        and:
        restTemplate.put(localUrl("/${key1}"), "oldValue")

        when:
        restTemplate.put(localUrl("/${key1}"), "newValue")

        then:
        restTemplate.getForEntity(localUrl("/${key1}"), String.class).body == "newValue"
    }
}