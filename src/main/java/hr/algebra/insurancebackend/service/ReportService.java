package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.dto.AcceptReportRequestDTO;
import hr.algebra.insurancebackend.dto.ReportDTO;
import hr.algebra.insurancebackend.dto.ReportRequestDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;

import java.util.List;

public interface ReportService {
    ReportDTO openAReport(ReportRequestDTO reportRequestDTO) throws ValidationException;

    List<ReportDTO> getRejectedReports();

    List<ReportDTO> getWaitingReports();

    List<ReportDTO> getAcceptedReports();

    List<ReportDTO> getAllReports();

    void acceptReport(AcceptReportRequestDTO acceptReportRequestDTO, long id) throws ValidationException;

    void rejectReport(long id);

    long getNumOfReports();

    long getNumOfReportsWaiting();
    long getNumOfReportsRejected();

    long getNumOfReportsAccepted();
}
