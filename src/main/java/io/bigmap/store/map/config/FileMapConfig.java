package io.bigmap.store.map.config;

import io.bigmap.store.map.FileMap;
import io.bigmap.store.map.infrastructure.InMemoryIndex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileMapConfig {

    @Bean
    FileMap fileMap() {
        // TODO move to props
        return new FileMap("/tmp/bigmap", new InMemoryIndex());
    }
}
