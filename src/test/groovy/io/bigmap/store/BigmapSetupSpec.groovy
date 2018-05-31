package io.bigmap.store

import io.bigmap.BaseIntegrationSpec
import org.springframework.web.client.RestTemplate

class BigmapSetupSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

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

    def "should return MASTER config when set to MASTER"() {
        given:
        restTemplate.put(localUrl("/admin/set-as-master"), [])

        when:
        def config = restTemplate.getForEntity(localUrl("/admin/config"), Map).body

        then:
        config == [
                role: 'MASTER',
                replicas: []
        ]
    }

}
