package team.seventhmile.tripforp.global.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.seventhmile.tripforp.domain.user.dto.VerificationCode;

@Configuration
public class EmailConfig {
    @Bean
    public Map<String, VerificationCode> verificationCodes() {
        return new ConcurrentHashMap<>();
    }

}
