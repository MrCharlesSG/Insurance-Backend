package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.domain.Report;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReportDTO {
    private long id;
    private InfoReportDriverDTO infoReportDriverA;
    private InfoReportDriverDTO infoReportDriverB;
    private Date date;
    private String place;
    private String details;

    public ReportDTO(Report report) {
        this.id = report.getId();
        this.infoReportDriverA = new InfoReportDriverDTO(report.getInfoReportDriverA());
        this.infoReportDriverB = new InfoReportDriverDTO(report.getInfoReportDriverB());
        this.date = report.getDate();
        this.place = report.getPlace();
        this.details = report.getDetails();
    }
}
