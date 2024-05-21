package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.repository.DriverRepository;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static hr.algebra.insurancebackend.utils.VehicleDriverRelation.addDriverToVehicle;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    public Optional<DriverDTO> createDriver(DriverDTO driverDTO) {
        try{
            checkDriverDTO(driverDTO);
        }catch (IllegalArgumentException e){
            return Optional.empty();
        }
        //Get vehicle
        Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
        Driver driver = new Driver(driverDTO);

        //SAVE
        Driver savedDriver = driverRepository.save(driver);
        vehicle.getDrivers().add(savedDriver);
        vehicleRepository.save(vehicle);

        return Optional.of(new DriverDTO(savedDriver));

    }

    public Optional<Driver> getById(Long id) {
        Optional<Driver> byId = driverRepository.findById(id);
        return byId;
    }

    private void checkDriverDTO(DriverDTO driverDTO) throws IllegalArgumentException {
        if (driverRepository.findByEmail(driverDTO.getEmail()).isPresent())
            throw new IllegalArgumentException("Driver with this email already exists");
    }

    public Optional<DriverDTO> associateDriver(String email) {
        Optional<Driver> byEmail = driverRepository.findByEmail(email);
        if(byEmail.isPresent()){
            Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
            vehicle.getDrivers().add(byEmail.get());
            vehicleRepository.save(vehicle);
            return driverRepository.findByEmail(email).map(DriverDTO::new);
        }
        return Optional.empty();
    }

    public Optional<DriverDTO> disassociateDriver(String email) {
        Optional<Driver> byEmail = driverRepository.findByEmail(email);
        if(byEmail.isPresent()){
            Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
            long driverByEmailID = byEmail.get().getId();
            vehicle.getDrivers().removeIf((driver) -> driver.getId()==driverByEmailID);
            vehicleRepository.save(vehicle);
            return driverRepository.findByEmail(email).map(DriverDTO::new);
        }
        return Optional.empty();
    }

    public List<DriverDTO> getAllDriversOfAuthenticatedVehicle() {
        Vehicle vehicle = vehicleService.getAuthenticatedVehicle();

        return vehicle
                .getDrivers()
                .stream()
                .map(DriverDTO::new)
                .toList();
    }

    public Optional<Driver> getByEmail(String email) {
        List<Driver> all = driverRepository.findAll();
        Optional<Driver> byEmail = driverRepository.findByEmail(email);
        return byEmail;
    }
}
