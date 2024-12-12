package team.seventhmile.tripforp.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${google.maps.api.key}")
    private String key;

    @Bean
    public WebClient googleWebClient() {
        return WebClient.builder()
            .baseUrl("https://places.googleapis.com/v1/places")
            .defaultHeader("X-Goog-Api-Key", key)
            .build();
    }

}
