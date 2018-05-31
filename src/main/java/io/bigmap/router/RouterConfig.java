package io.bigmap.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RouterConfig {

    @Bean
    public RouterSetupRepository routerSetup(RestTemplate restTemplate) {
        return new SyncRouterSetupRepository(restTemplate);
    }
}