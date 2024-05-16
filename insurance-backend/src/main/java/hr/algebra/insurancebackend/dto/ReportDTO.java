package hr.algebra.insurancebackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Getter
public class ReportDTO {
    private long id;
    private InfoReportDriverDTO infoReportDriverA;
    private InfoReportDriverDTO infoReportDriverB;
    private Date date;
    private String place;
    private String details;
}
