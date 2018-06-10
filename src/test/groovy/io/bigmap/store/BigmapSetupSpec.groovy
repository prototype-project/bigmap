package io.bigmap.store

import io.bigmap.BaseIntegrationSpec
import org.springframework.web.client.RestTemplate

class BigmapSetupSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    def "should return REPLICA config when set to REPLICA"() {
        given:
        restTemplate.put(localUrl("/map/admin/set-as-replica"), null)

        when:
        def config = restTemplate.getForEntity(localUrl("/map/admin/config"), Map).body

        then:
        config == [
                address: localUrl(''),
                role: 'REPLICA'
        ]
    }

    def "should return MASTER config when set to MASTER"() {
        given:
        restTemplate.put(localUrl("/map/admin/set-as-master"), [])

        when:
        def config = restTemplate.getForEntity(localUrl("/map/admin/config"), Map).body

        then:
        config == [
                address: localUrl(''),
                role: 'MASTER',
                replicas: []
        ]
    }

}
