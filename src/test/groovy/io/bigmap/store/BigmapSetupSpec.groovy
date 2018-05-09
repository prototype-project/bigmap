package io.bigmap.store

import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class BigmapSetupSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

//    def "should be set up as MASTER by default"() {
//        when:
//        def config = restTemplate.getForEntity(localUrl("/admin/config"), Map).body
//
//        then:
//        config == [
//                role: 'MASTER',
//                replicas: []
//        ]
//
//    }

    def "should accept put and get as MASTER"() {
        given:
        String key1 = UUID.randomUUID()

        and:
        restTemplate.put(localUrl("/admin/set-as-master"), [])

        when:
        restTemplate.put(localUrl("/map/${key1}"), "value")

        then:
        restTemplate.getForEntity(localUrl("/map/${key1}"), String.class).body == "value"
    }

    def "should accept get as REPLICA"() {
        given:
        String key1 = UUID.randomUUID()

        and:
        restTemplate.put(localUrl("/admin/set-as-master"), [])

        and:
        restTemplate.put(localUrl("/map/${key1}"), 'value')

        and:
        restTemplate.put(localUrl("/admin/set-as-replica"), null)

        when:
        def result = restTemplate.getForEntity(localUrl("/map/${key1}"), String.class).body

        then:
        result == 'value'
    }

    def "should return REPLICA config when set to REPLICA"() {
        given:
        restTemplate.put(localUrl("/admin/set-as-replica"), null)

        when:
        def config = restTemplate.getForEntity(localUrl("/admin/config"), Map).body

        then:
        config == [
                role: 'REPLICA'
        ]

    }

    def "should return 400 when putting to REPLICA"() {
        given:
        restTemplate.put(localUrl("/admin/set-as-replica"), null)

        and:
        String key1 = UUID.randomUUID()

        when:
        restTemplate.put(localUrl("/map/${key1}"), 'value')

        then:
        thrown(HttpClientErrorException)
    }
}
