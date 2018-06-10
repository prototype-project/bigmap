package io.bigmap.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RouterConfig {

    @Bean
    RouterSetupRepository routerSetup(RestTemplate restTemplate) {
        RouterSetupRepository httpRouterSetupRepository = new HttpRouterSetupRepository(restTemplate);
        return new CachedRouterSetupRepository(httpRouterSetupRepository);
    }

    @Bean
    Router syncRouter(
            RestTemplate restTemplate,
            RouterSetupRepository routerSetup) {
        return new HttpRouter(restTemplate, routerSetup);
    }
}