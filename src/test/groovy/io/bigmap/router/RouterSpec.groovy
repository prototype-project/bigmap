package io.bigmap.router

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.json.JsonOutput
import io.bigmap.BaseIntegrationSpec
import org.apache.commons.lang3.StringUtils
import org.springframework.web.client.RestTemplate

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.okJson
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class Master {
    WireMockServer server
    List<Replica> replicas

    Master(WireMockServer server, List<Replica> replicas) {
        this.server = server
        this.replicas = replicas
    }

    def start() {
        replicas.forEach({r -> r.server.start()})
        server.start()
    }

    def stop() {
        replicas.forEach({r -> r.server.stop()})
        server.stop()
    }

    def stubConfig() {
        server.stubFor(get(urlEqualTo("/map/admin/config"))
                .willReturn(okJson(JsonOutput.toJson([
                    role: 'MASTER',
                    address: StringUtils.chop(server.url("")),
                    replicas: replicas.collect{r -> StringUtils.chop(r.server.url(""))}
        ]
        ))))
        server.stubFor(put(urlMatching("/map/.*"))
                .willReturn(aResponse()
                .withStatus(200)))
    }

    boolean gotPut(String key) {
        server.countRequestsMatching(putRequestedFor(urlPathMatching("/map/$key")).build()).count == 1
    }
}

class Replica {
    WireMockServer server

    Replica(WireMockServer server) {
        this.server = server
    }
}

class RouterSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()
    static int FIRST_MASTER = 8081
    static int SECOND_MASTER = 8082

    Map<Integer, List<String>> storeSetup = [
            8081: ['8090', '8091'],
            8082: ['8092', '8093']
    ]

    List<Master> masters

    def setup() {
        this.masters = [FIRST_MASTER, SECOND_MASTER].collect {masterPort->
            List<String> replicaPorts = storeSetup[masterPort]
            def replicas = replicaPorts.collect { replicaPort ->
                new Replica(new WireMockServer(wireMockConfig().port(replicaPort.toInteger())))
            }
            def master = new WireMockServer(wireMockConfig().port(masterPort.toInteger()))
            new Master(master, replicas)
        }

        this.masters.forEach{m ->
            m.start()
        }

        this.masters.forEach{m ->
            m.stubConfig()
        }
    }

    def cleanup() {
        this.masters.each {m -> m.stop()}
    }

    def findMaster(int masterPort) {
        this.masters.find {m -> m.server.port() == masterPort}
    }

    def "should setup router"() {
        when:
        restTemplate.put(localUrl("/router/admin/config"),
                ['http://localhost:8081', 'http://localhost:8082'], List)

        then:
        restTemplate.getForEntity(localUrl("/router/admin/config"), List).body == [
                [
                        master  : 'http://localhost:8081',
                        replicas: ['http://localhost:8090', 'http://localhost:8091']
                ],
                [
                        master  : 'http://localhost:8082',
                        replicas: ['http://localhost:8092', 'http://localhost:8093']
                ]
        ]
    }

    def "should route PUT to single master"() {
        given:
        restTemplate.put(localUrl("/router/admin/config"),
                ['http://localhost:8081', 'http://localhost:8082'], List)

        and:
        String key = UUID.randomUUID().toString()

        when:
        restTemplate.put(localUrl("/router/$key"), 'someValue', String)

        then:
        findMaster(FIRST_MASTER).gotPut(key) != findMaster(SECOND_MASTER).gotPut(key)
    }
}
