package io.bigmap.store

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import io.bigmap.BaseIntegrationSpec
import org.springframework.web.client.RestTemplate

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class StoreReplicationSpec extends BaseIntegrationSpec {

    RestTemplate clientRestTemplate = new RestTemplate()
    WireMockServer server

    def setup() {
        this.server = new WireMockServer(wireMockConfig().port(8081))
        server.start()
    }

    def cleanup() {
        this.server.stop()
    }

    def "should notify all replicas on put if configured as master"() {
        given:
        String key = UUID.randomUUID()

        clientRestTemplate.put(localUrl("/map/admin/set-as-master"), ['http://localhost:8081'])

        when:
        clientRestTemplate.put(localUrl("/map/${key}"), 'value')

        then:
        postRequestedFor(urlEqualTo("/map/${key}"))
                .withRequestBody(new EqualToPattern('value'))
    }

    def "should notify all replicas on delete if configured as master"() {
        given:
        String key = UUID.randomUUID()

        clientRestTemplate.put(localUrl("/map/admin/set-as-master"), ['http://localhost:8081'])

        when:
        clientRestTemplate.delete(localUrl("/map/${key}"))

        then:
        deleteRequestedFor(urlEqualTo("/map/${key}"))
    }

}
