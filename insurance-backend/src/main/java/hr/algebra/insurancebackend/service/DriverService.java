package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public interface DriverService {
    Optional<DriverDTO> createDriver(DriverDTO driverDTO) throws ValidationException;

    Optional<Driver> getById(Long id);

    Optional<DriverDTO> associateDriver(String email) throws ValidationException;

    Optional<DriverDTO> disassociateDriver(String email) throws ValidationException;

    List<DriverDTO> getAllDriversOfAuthenticatedVehicle();

    Optional<Driver> getByEmail(String email) throws ValidationException;

    List<DriverDTO> getByVehicle(String plate);
}
