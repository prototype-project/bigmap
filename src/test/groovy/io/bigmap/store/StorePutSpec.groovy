package io.bigmap.store

import io.bigmap.BaseIntegrationSpec
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class StorePutSpec extends BaseIntegrationSpec {
    RestTemplate restTemplate = new RestTemplate()

    def "should return 400 NULL_VALUE when value not provided"() {
        given:
        String key1 = UUID.randomUUID()

        when:
        restTemplate.put(localUrl("/map/${key1}"), null)

        then:
        thrown(HttpClientErrorException)
    }

    def "should return 500 SERVER_ERROR when critical error occurs"() {
    }
}
