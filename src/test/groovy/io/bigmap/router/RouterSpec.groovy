package io.bigmap.router

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.json.JsonOutput
import io.bigmap.BaseIntegrationSpec
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.delete
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
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
    static String TEST_KEY = "KEY"
    static String TEST_NOT_FOUND_KEY = "NOT_FOUND_KEY"

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

    def stub() {
        server.stubFor(get(urlEqualTo("/map/admin/config"))
                .willReturn(okJson(JsonOutput.toJson([
                    role: 'MASTER',
                    address: StringUtils.chop(server.url("")),
                    replicas: replicas.collect{r -> StringUtils.chop(r.server.url(""))}
        ]
        ))))
        server.stubFor(put(urlMatching("/map/${TEST_KEY}"))
                .willReturn(aResponse()
                .withStatus(200)))

        server.stubFor(delete(urlMatching("/map/${TEST_KEY}"))
                .willReturn(aResponse()
                .withStatus(200)))

        replicas.each {r -> r.stub()}
    }

    boolean gotPut(String key) {
        server.countRequestsMatching(putRequestedFor(urlPathMatching("/map/$key")).build()).count == 1
    }

    boolean gotGet(String key) {
        server.countRequestsMatching(getRequestedFor(urlPathMatching("/map/$key")).build()).count == 1
    }

    boolean gotDelete(String key) {
        server.countRequestsMatching(deleteRequestedFor(urlPathMatching("/map/$key")).build()).count == 1
    }

    Replica getReplica(int replicaPort) {
        return replicas.find {r -> r.server.port() == replicaPort}
    }
}

class Replica {
    WireMockServer server

    Replica(WireMockServer server) {
        this.server = server
    }

    def stub() {
        server.stubFor(get(urlMatching("/map/${Master.TEST_KEY}"))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody("someValue")
        ))

        server.stubFor(get(urlMatching("/map/${Master.TEST_NOT_FOUND_KEY}"))
                .willReturn(aResponse()
                .withStatus(404)
                .withBody("KEY_NOT_FOUND")
        ))
    }

    boolean gotGet(String key) {
        server.countRequestsMatching(getRequestedFor(urlPathMatching("/map/$key")).build()).count == 1
    }
}

class RouterSpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()
    static int FIRST_MASTER = 8081
    static int SECOND_MASTER = 8082
    static int FIRST_MASTER_FIRST_REPLICA = 8090
    static int FIRST_MASTER_SECOND_REPLICA = 8091
    static int SECOND_MASTER_FIRST_REPLICA = 8092
    static int SECOND_MASTER_SECOND_REPLICA = 8093

    Map<Integer, List<Integer>> storeSetup = [
            8081: [FIRST_MASTER_FIRST_REPLICA, FIRST_MASTER_SECOND_REPLICA],
            8082: [SECOND_MASTER_FIRST_REPLICA, SECOND_MASTER_SECOND_REPLICA]
    ]

    List<Master> masters

    def setup() {
        this.masters = [FIRST_MASTER, SECOND_MASTER].collect {masterPort->
            List<Integer> replicaPorts = storeSetup[masterPort]
            def replicas = replicaPorts.collect { replicaPort ->
                new Replica(new WireMockServer(wireMockConfig().port(replicaPort)))
            }
            def master = new WireMockServer(wireMockConfig().port(masterPort))
            new Master(master, replicas)
        }

        this.masters.forEach{m ->
            m.start()
        }

        this.masters.forEach{m ->
            m.stub()
        }

        restTemplate.put(localUrl("/router/admin/config"),
                ['http://localhost:8081', 'http://localhost:8082'], List)
    }

    def cleanup() {
        this.masters.each {m -> m.stop()}
    }

    def findMaster(int masterPort) {
        this.masters.find {m -> m.server.port() == masterPort}
    }

    def "should setup router"() {
        expect:
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
        when:
        restTemplate.put(localUrl("/router/${Master.TEST_KEY}"), 'someValue', String)

        then:
        findMaster(FIRST_MASTER).gotPut(Master.TEST_KEY)
        !findMaster(SECOND_MASTER).gotPut(Master.TEST_KEY)
    }

    def "should route GET to single master replica"() {
        when:
        def response = restTemplate.getForEntity(localUrl("/router/${Master.TEST_KEY}"), String)

        then:
        findMaster(FIRST_MASTER).getReplica(FIRST_MASTER_FIRST_REPLICA).gotGet(Master.TEST_KEY)
        !findMaster(FIRST_MASTER).getReplica(FIRST_MASTER_SECOND_REPLICA).gotGet(Master.TEST_KEY)
        !findMaster(SECOND_MASTER).getReplica(SECOND_MASTER_FIRST_REPLICA).gotGet(Master.TEST_KEY)
        !findMaster(SECOND_MASTER).getReplica(SECOND_MASTER_SECOND_REPLICA).gotGet(Master.TEST_KEY)

        and:
        !findMaster(FIRST_MASTER).gotGet(Master.TEST_KEY)
        !findMaster(SECOND_MASTER).gotGet(Master.TEST_KEY)

        and:
        response.getBody() == "someValue"
    }

    def "should route DELETE to single master"() {
        when:
        restTemplate.delete(localUrl("/router/${Master.TEST_KEY}"))

        then:
        findMaster(FIRST_MASTER).gotDelete(Master.TEST_KEY)
        !findMaster(SECOND_MASTER).gotDelete(Master.TEST_KEY)
    }

    def "should return errors from map back to client"() {
        when:
        restTemplate.getForEntity(localUrl("/router/${Master.TEST_NOT_FOUND_KEY}"), String.class)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
        ex.responseBodyAsString == "KEY_NOT_FOUND"
    }
}
