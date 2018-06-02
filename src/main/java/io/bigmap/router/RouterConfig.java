package io.bigmap.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RouterConfig {

    @Bean
    RouterSetupRepository routerSetup(RestTemplate restTemplate) {
        return new SyncRouterSetupRepository(restTemplate);
    }

    @Bean
    Router syncRouter(
            RestTemplate restTemplate,
            RouterSetupRepository routerSetup) {
        return new SyncRouter(restTemplate, routerSetup);
    }
}