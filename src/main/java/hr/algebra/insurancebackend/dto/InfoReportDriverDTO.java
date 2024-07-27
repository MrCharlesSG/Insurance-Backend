package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.domain.InfoReportDriver;
import lombok.*;

@Getter
@NoArgsConstructor
@Setter
@Builder
@AllArgsConstructor
public class InfoReportDriverDTO {
    private long id;
    private VehicleInfoDTO vehicle;
    private DriverDTO driver;
    private String damages;
    private String status;

    public InfoReportDriverDTO(InfoReportDriver infoReportDriver) {
        this.id = infoReportDriver.getId();
        this.vehicle = new VehicleInfoDTO(infoReportDriver.getVehicle());
        this.driver = new DriverDTO(infoReportDriver.getDriver());
        this.damages = infoReportDriver.getDamages();
        this.status = infoReportDriver.getStatus().name();
    }
}
