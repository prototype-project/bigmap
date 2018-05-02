package io.bigmap.store

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.springframework.web.client.RestTemplate

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class StoreReplicationSpec extends BaseIntegrationSpec {

    RestTemplate clientRestTemplate = new RestTemplate()
    WireMockServer wireMockServer1

    def setup() {
        this.wireMockServer1 = new WireMockServer(wireMockConfig().port(8081))
        wireMockServer1.start()
    }

    def cleanup() {
        this.wireMockServer1.stop()
    }

    def "should notify all replicas on put if configured as master"() {
        given:
        String key1 = UUID.randomUUID()

        clientRestTemplate.put(localUrl("/admin/set-as-master"), ['http://localhost:8081'])

        when:
        clientRestTemplate.put(localUrl("/${key1}"), 'value')

        then:
        postRequestedFor(urlEqualTo("/${key1}"))
                .withRequestBody(new EqualToPattern('value'))
    }

}
