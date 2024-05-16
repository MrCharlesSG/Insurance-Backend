package hr.algebra.insurancebackend.utils;

import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.domain.Vehicle;

public class VehicleDriverRelation {

    public static void addDriverToVehicle(Vehicle vehicle, Driver driver) {
        vehicle.getDrivers().add(driver);
        driver.getVehicles().add(vehicle);
    }

    public static void removeDriverFromVehicle(Vehicle vehicle, Driver driver) {
        vehicle.getDrivers().remove(driver);
        driver.getVehicles().remove(vehicle);
    }
}