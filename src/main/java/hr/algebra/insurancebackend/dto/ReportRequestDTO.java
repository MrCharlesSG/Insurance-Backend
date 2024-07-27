package hr.algebra.insurancebackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class ReportRequestDTO {
    private long driverAId;
    private long driverBId;
    private String vehicleB;
    private String damages;
    private Date date;
    private String place;
    private String details;
}
