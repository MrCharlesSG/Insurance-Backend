package hr.algebra.insurancebackend.configuration;

import hr.algebra.insurancebackend.service.ReportService;
import hr.algebra.insurancebackend.service.VehicleService;
import io.micrometer.core.instrument.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportMetricsConfig {

    @Bean
    public Gauge averageReportsPerVehicleGauge(ReportService reportService, VehicleService vehicleService, MeterRegistry meterRegistry) {
        return Gauge.builder("reports.per.vehicle.avg", reportService, rs -> {
                    long numberOfVehicles = vehicleService.getNumOfVehicles();
                    long numberOfOpenedReports = reportService.getNumOfReports();
                    return numberOfVehicles == 0 ? 0 : (double) numberOfOpenedReports / numberOfVehicles;
                })
                .description("Average number of reports per vehicle")
                .register(meterRegistry);
    }
    @Bean
    public Gauge averageAcceptedReportsPerVehicleGauge(ReportService reportService, VehicleService vehicleService, MeterRegistry meterRegistry) {
        return Gauge.builder("reports.accepted.per.vehicle.avg", reportService, rs -> {
                    long numberOfVehicles = vehicleService.getNumOfVehicles();
                    long numberOfAcceptedReports = reportService.getNumOfReportsAccepted();
                    return numberOfVehicles == 0 ? 0 : (double) numberOfAcceptedReports / numberOfVehicles;
                })
                .description("Average number of accepted reports per vehicle")
                .register(meterRegistry);
    }
    @Bean
    public Gauge averageRejectedReportsPerVehicleGauge(ReportService reportService, VehicleService vehicleService, MeterRegistry meterRegistry) {
        return Gauge.builder("reports.rejected.per.vehicle.avg", reportService, rs -> {
                    long numberOfVehicles = vehicleService.getNumOfVehicles();
                    long numberOfRejectedReports = reportService.getNumOfReportsRejected();
                    return numberOfVehicles == 0 ? 0 : (double) numberOfRejectedReports / numberOfVehicles;
                })
                .description("Average number of reports per vehicle")
                .register(meterRegistry);
    }
    @Bean
    public Gauge averageWaitingReportsPerVehicleGauge(ReportService reportService, VehicleService vehicleService, MeterRegistry meterRegistry) {
        return Gauge.builder("reports.waiting.per.vehicle.avg", reportService, rs -> {
                    long numberOfVehicles = vehicleService.getNumOfVehicles();
                    long numberOfWaitingReports = reportService.getNumOfReportsWaiting();
                    return numberOfVehicles == 0 ? 0 : (double) numberOfWaitingReports / numberOfVehicles;
                })
                .description("Average number of reports per vehicle")
                .register(meterRegistry);
    }

    @Bean
    public DistributionSummary reportSummary(MeterRegistry meterRegistry) {
        return DistributionSummary.builder("reports.opened")
                .description("Number of opened reports")
                .register(meterRegistry);
    }
}
