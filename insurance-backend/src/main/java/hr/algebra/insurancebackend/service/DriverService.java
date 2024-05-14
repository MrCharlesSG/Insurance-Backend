package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.repository.DriverRepository;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    public Optional<DriverDTO> createDriver(DriverDTO driverDTO){
        //Get vehicle
        Optional<Vehicle> authenticatedVehicle = vehicleService.getAuthenticatedVehicle();
        if(authenticatedVehicle.isEmpty()) throw new UsernameNotFoundException("Could not found the authenticated vehicle");
        //Associate
        Driver driver = new Driver(driverDTO);
        Vehicle vehicle = authenticatedVehicle.get();
        addDriverToVehicle(vehicle, driver);

        //SAVE
        vehicleRepository.save(vehicle);
        Driver savedDriver = driverRepository.save(driver);

        return Optional.of(new DriverDTO(savedDriver));

    }

}
