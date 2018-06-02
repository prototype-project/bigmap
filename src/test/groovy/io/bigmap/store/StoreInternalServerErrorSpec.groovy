package io.bigmap.store

import io.bigmap.BaseIntegrationSpec
import io.bigmap.common.CriticalError
import io.bigmap.store.domain.StoreMap
import io.bigmap.store.infrastructure.FileMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes = [MockConfig])
class StoreInternalServerErrorSpec extends BaseIntegrationSpec {

    @Autowired
    StoreMap storeMap

    RestTemplate restTemplate = new RestTemplate()

    def "should return 500 SERVER_ERROR when critical error occurs"() {
        given:
        String key = UUID.randomUUID()
        String value = "value"

        and:
        storeMap.put(key, value) >> {throw new CriticalError()}

        when:
        restTemplate.put(localUrl("/map/${key}"), value)

        then:
        thrown(HttpServerErrorException)
    }

    @Configuration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory();

        @Bean
        @Primary
        FileMap storeMap() {
            return detachedMockFactory.Stub(FileMap)
        }
    }
}
