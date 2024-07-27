package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.dto.AcceptReportRequestDTO;
import hr.algebra.insurancebackend.dto.ReportDTO;
import hr.algebra.insurancebackend.dto.ReportRequestDTO;
import hr.algebra.insurancebackend.service.ReportService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static hr.algebra.insurancebackend.constants.Constants.Methods.*;
import static hr.algebra.insurancebackend.constants.Constants.Metrics.*;
import static hr.algebra.insurancebackend.constants.Constants.ReportControllerEndpoints.*;

@RestController
@RequestMapping(REPORT)
@AllArgsConstructor
public class ReportController {

    private ReportService reportService;
    private final MeterRegistry meterRegistry;
    private final DistributionSummary openedReportsPerVehicleSummary;

    @GetMapping(REJECTED)
    public List<ReportDTO> getRejectedReports() {
        return reportService.getRejectedReports();
    }

    @GetMapping(WAITING)
    public List<ReportDTO> getWaitingReports() {
       return reportService.getWaitingReports();
    }

    @GetMapping(ACCEPTED)
    public List<ReportDTO> getAcceptedReports() {
        return reportService.getAcceptedReports();
    }

    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportService.getAllReports();
    }

    @PostMapping
    public ReportDTO openReport(@RequestBody ReportRequestDTO reportRequestDTO) {
        try {
            ReportDTO report = reportService.openAReport(reportRequestDTO);
            openedReportsPerVehicleSummary.record(1); // Record a report opening event
            return report;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping(ACCEPT_ID)
    public void acceptReport(@RequestBody AcceptReportRequestDTO acceptReportRequestDTO, @PathVariable long id) {
        try {
            reportService.acceptReport(acceptReportRequestDTO, id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping(REJECT_ID)
    public void rejectReport(@PathVariable long id) {
        try {
            reportService.rejectReport(id);
        } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
