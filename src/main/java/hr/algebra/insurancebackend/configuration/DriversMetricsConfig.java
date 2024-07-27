package hr.algebra.insurancebackend.configuration;

import io.micrometer.core.instrument.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class DriversMetricsConfig {

    @Bean
    public Counter createDriverFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("driver.create.failure")
                .description("Number of failures in creating drivers")
                .register(meterRegistry);
    }

    @Bean
    public AtomicInteger associateDriverConcurrency() {
        return new AtomicInteger(0);
    }

    @Bean
    public AtomicInteger disassociateDriverConcurrency() {
        return new AtomicInteger(0);
    }

    @Bean
    public Gauge associateDriverConcurrencyGauge(AtomicInteger associateDriverConcurrency, MeterRegistry meterRegistry) {
        return Gauge.builder("driver.associate.concurrency", associateDriverConcurrency, AtomicInteger::get)
                .description("Number of concurrent associateDriver requests")
                .register(meterRegistry);
    }

    @Bean
    public Gauge disassociateDriverConcurrencyGauge(AtomicInteger disassociateDriverConcurrency, MeterRegistry meterRegistry) {
        return Gauge.builder("driver.disassociate.concurrency", disassociateDriverConcurrency, AtomicInteger::get)
                .description("Number of concurrent disassociateDriver requests")
                .register(meterRegistry);
    }

    @Bean
    public Timer getDriverByIdTimer(MeterRegistry meterRegistry) {
        return Timer.builder("driver.get.byId.time")
                .description("Time taken to get driver by ID")
                .register(meterRegistry);
    }

    @Bean
    public Timer getAllDriversTimer(MeterRegistry meterRegistry) {
        return Timer.builder("driver.get.all.time")
                .description("Time taken to get all drivers")
                .register(meterRegistry);
    }

    @Bean
    public Timer getDriverByVehicleTimer(MeterRegistry meterRegistry) {
        return Timer.builder("driver.get.byVehicle.time")
                .description("Time taken to get drivers by vehicle")
                .register(meterRegistry);
    }

    @Bean
    public Timer getDriverByEmailTimer(MeterRegistry meterRegistry) {
        return Timer.builder("driver.get.byEmail.time")
                .description("Time taken to get driver by email")
                .register(meterRegistry);
    }
}
