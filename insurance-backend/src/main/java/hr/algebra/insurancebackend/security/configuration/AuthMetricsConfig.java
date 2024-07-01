package hr.algebra.insurancebackend.security.configuration;

import io.micrometer.core.instrument.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthMetricsConfig {

    @Bean
    public Counter loginCounter(MeterRegistry meterRegistry) {
        return Counter.builder("auth.login")
                .description("Number of login requests")
                .register(meterRegistry);
    }

    @Bean
    public Counter logoutCounter(MeterRegistry meterRegistry) {
        return Counter.builder("auth.logout")
                .description("Number of logout requests")
                .register(meterRegistry);
    }

    @Bean
    public Counter registerCounter(MeterRegistry meterRegistry) {
        return Counter.builder("auth.register")
                .description("Number of register requests")
                .register(meterRegistry);
    }
}
