package hr.algebra.insurancebackend.command;

import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleCommand {
    private SignUpVehicleDTO user;
    private String brand;
    private String model;
    private Year manufacturingYear;
}
