package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.provider.UsernameProvider;
import lombok.*;

import java.time.Year;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoDTO implements UsernameProvider {
    private String plate;

    private String brand;

    private String model;

    private Year manufacturingYear;

    private long id;
    public VehicleInfoDTO(Vehicle vehicle) {
        this.brand = vehicle.getBrand();
        this.plate = vehicle.getUserInfo().getUsername();
        this.model = vehicle.getModel();
        this.id= vehicle.getId();
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof VehicleInfoDTO that)) return false;
        return id == that.id && Objects.equals(plate, that.plate) && Objects.equals(brand, that.brand) && Objects.equals(model, that.model) && Objects.equals(manufacturingYear, that.manufacturingYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plate, brand, model, manufacturingYear, id);
    }
}
