package hr.algebra.insurancebackend.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VehicleCommand {
    private String plate;
    private String brand;
    private String model;
    private Year manufacturingYear;
}
