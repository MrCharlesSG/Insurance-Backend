package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.dto.AcceptReportRequestDTO;
import hr.algebra.insurancebackend.dto.ReportDTO;
import hr.algebra.insurancebackend.dto.ReportRequestDTO;
import hr.algebra.insurancebackend.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("report")
@AllArgsConstructor
public class ReportController {


    @Autowired
    private ReportService reportService;
    @GetMapping("/rejected")
    public List<ReportDTO> getRejectedReports() {
        return reportService.getRejectedReports();
    }

    @GetMapping("/waiting")
    public List<ReportDTO> getWaitingReports() {
        return reportService.getWaitingReports();
    }

    @GetMapping("/accepted")
    public List<ReportDTO> getAcceptedReports() {
        return reportService.getAcceptedReports();
    }

    @GetMapping
    public List<ReportDTO> getAllReports() {
        return reportService.getAllReports();
    }

    @PostMapping
    public ReportDTO openReport(@RequestBody ReportRequestDTO reportRequestDTO){
        try {
            ReportDTO reportDTO = reportService.openAReport(reportRequestDTO);
            return reportDTO;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/accept/{id}")
    public void acceptReport(@RequestBody AcceptReportRequestDTO acceptReportRequestDTO, @PathVariable long id){
        try {
            reportService.acceptReport(acceptReportRequestDTO, id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/reject/{id}")
    public void rejectReport(@PathVariable long id){
        try {
            reportService.rejectReport(id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
