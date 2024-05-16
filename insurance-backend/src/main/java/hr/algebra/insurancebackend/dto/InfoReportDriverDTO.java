package hr.algebra.insurancebackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InfoReportDriverDTO {
    private long id;
    private long vehicleId;
    private long driverId;
    private String damages;
    private boolean accepted;
}
