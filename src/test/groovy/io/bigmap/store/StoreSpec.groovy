package io.bigmap.store

import org.springframework.web.client.RestTemplate

class StoreSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    def "should add value to store and return value from store"() {
        given:
        String key = 'johnSmith'
        String value = 'agentSmith'

        when:
        restTemplate.put(localUrl("/${key}"), value)

        then:
        restTemplate.getForEntity(localUrl("/${key}"), String.class).body == value
    }
}