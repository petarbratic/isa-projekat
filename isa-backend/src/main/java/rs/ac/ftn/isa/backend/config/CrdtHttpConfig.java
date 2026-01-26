package rs.ac.ftn.isa.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CrdtHttpConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}