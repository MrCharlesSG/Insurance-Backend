package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.command.VehicleCommand;
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

    public VehicleInfoDTO(VehicleCommand vehicleCommand){
        this.brand= vehicleCommand.getBrand();
        this.manufacturingYear=vehicleCommand.getManufacturingYear();
        this.model=vehicleCommand.getModel();
        this.plate= vehicleCommand.getPlate();
    }

    @Override
    public String getUsername() {
        return plate;
    }

}
