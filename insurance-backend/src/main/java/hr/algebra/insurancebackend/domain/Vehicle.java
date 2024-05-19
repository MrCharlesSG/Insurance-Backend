package hr.algebra.insurancebackend.domain;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.Set;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "VEHICLES")
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @OneToOne
    @JoinColumn(name = "user_info", referencedColumnName = "id")
    private UserInfo userInfo;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "manufacturing_Year")
    private Year manufacturingYear;

    @ManyToMany
    @JoinTable(
            name = "VEHICLE_DRIVER_MAPPING",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "driver_id"))
    private Set<Driver> drivers;

    public Vehicle(VehicleCommand vehicleCommand, UserInfo userInfo) {
        this.brand = vehicleCommand.getBrand();
        this.model = vehicleCommand.getModel();
        this.manufacturingYear = vehicleCommand.getManufacturingYear();
        this.userInfo = userInfo;
    }

    public Vehicle(VehicleInfoDTO vehicle, UserInfo userInfo) {
        this.brand = vehicle.getBrand();
        this.model = vehicle.getModel();
        this.manufacturingYear = vehicle.getManufacturingYear();
        this.userInfo = userInfo;
    }

}
