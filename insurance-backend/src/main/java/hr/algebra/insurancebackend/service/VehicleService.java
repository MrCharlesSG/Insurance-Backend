package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.security.domain.UserInfo;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Optional<VehicleInfoDTO> createVehicle(VehicleInfoDTO vehicle, UserInfo userInfo);

    Optional<Vehicle> getVehicleOfUserInfo(UserInfo userInfo);

    Vehicle getAuthenticatedVehicle();

    VehicleInfoDTO update(Long id, VehicleCommand vehicleCommand);

    List<VehicleInfoDTO> getAll();

    Optional<VehicleInfoDTO> getById(Long id);

    void deleteVehicle(Long id);

    Optional<VehicleInfoDTO> getByPlate(String plate);

    long getNumOfVehicles();
}
