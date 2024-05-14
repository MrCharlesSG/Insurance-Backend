package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.provider.UsernameProvider;
import lombok.*;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoDTO implements UsernameProvider {
    private String plate;

    private String brand;

    private String model;

    private Year manufacturingYear;

    public VehicleInfoDTO(Vehicle vehicle) {
        this.brand = vehicle.getBrand();
        this.plate = vehicle.getUserInfo().getUsername();
        this.model = vehicle.getModel();
        this.manufacturingYear = vehicle.getManufacturingYear();
    }

    @Override
    public String getUsername() {
        return plate;
    }
}
