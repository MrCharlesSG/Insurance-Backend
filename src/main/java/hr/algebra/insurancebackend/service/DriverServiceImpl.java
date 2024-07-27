package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.repository.DriverRepository;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    @Override
    public Optional<DriverDTO> createDriver(DriverDTO driverDTO) throws ValidationException {
        return Optional.ofNullable(driverDTO)
                .filter(dto -> {
                    try {
                        checkDriverDTO(dto);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .map(dto -> {
                    Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
                    Driver driver = new Driver(dto);
                    Driver savedDriver = driverRepository.save(driver);
                    vehicle.getDrivers().add(savedDriver);
                    vehicleRepository.save(vehicle);
                    return new DriverDTO(savedDriver);
                });
    }


    @Override
    public Optional<DriverDTO> getById(Long id) {
        return Optional
                .ofNullable(id)
                .flatMap(driverRepository::findById)
                .map(DriverDTO::new);
    }

    private void checkDriverDTO(DriverDTO driverDTO) throws IllegalArgumentException {
        Optional.ofNullable(driverDTO)
                .map(DriverDTO::getEmail)
                .flatMap(driverRepository::findByEmail)
                .ifPresent(driver -> {
                    throw new IllegalArgumentException("Driver with this email already exists");
                });
    }


    @Override
    public Optional<DriverDTO> associateDriver(String email) throws ValidationException {
        return Optional.ofNullable(email)
                .flatMap(driverRepository::findByEmail)
                .map(driver -> {
                    Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
                    vehicle.getDrivers().add(driver);
                    vehicleRepository.save(vehicle);
                    return new DriverDTO(driver);
                });
    }


    @Override
    public Optional<DriverDTO> disassociateDriver(String email) throws ValidationException {
        return Optional.ofNullable(email)
                .flatMap(driverRepository::findByEmail)
                .map(driver -> {
                    Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
                    vehicle.getDrivers().removeIf(d -> d.getId()==driver.getId());
                    vehicleRepository.save(vehicle);
                    return new DriverDTO(driver);
                });
    }


    @Override
    public List<DriverDTO> getAllDriversOfAuthenticatedVehicle() {
        return Optional.ofNullable(vehicleService.getAuthenticatedVehicle())
                .map(Vehicle::getDrivers)
                .stream()
                .flatMap(Set::stream)
                .map(DriverDTO::new)
                .toList();
    }


    @Override
    public Optional<DriverDTO> getByEmail(String email) throws ValidationException {
        return Optional.ofNullable(email)
                .flatMap(driverRepository::findByEmail)
                .map(DriverDTO::new);
    }


    @Override
    public List<DriverDTO> getByVehicle(String plate) {
        return vehicleRepository
                .findByPlate(plate)
                .map(vehicle -> vehicle.getDrivers().stream()
                        .map(DriverDTO::new)
                        .toList())
                .orElseThrow(() -> new UsernameNotFoundException("Plate not found"));
    }
}
