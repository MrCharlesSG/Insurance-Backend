package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import hr.algebra.insurancebackend.security.service.AuthService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    @Lazy
    private AuthService authService;

    public Optional<VehicleInfoDTO> createVehicle(VehicleInfoDTO vehicle, UserInfo userInfo) {
        return Optional.of(new VehicleInfoDTO(vehicleRepository.save(new Vehicle(vehicle, userInfo))));
    }

    public Optional<Vehicle> getVehicleOfUserInfo(UserInfo userInfo) {
        Optional<Vehicle> byUserInfoId = vehicleRepository.findByUserInfoId(userInfo.getId());
        return byUserInfoId;
    }

    public Vehicle getAuthenticatedVehicle(){
        UserInfo currentUser = authService.getCurrentUser();
        return getVehicleOfUserInfo(currentUser)
                .orElseThrow(() -> new UsernameNotFoundException("There is no vehicle authenticated"));
    }


    public VehicleInfoDTO update(Long id, VehicleCommand vehicleCommand) {
        Optional<Vehicle> byId = vehicleRepository.findById(id);
        if(byId.isEmpty()) throw new IllegalArgumentException("Vehicle Not found");
        Vehicle vehicle = new Vehicle(vehicleCommand, byId.get().getUserInfo());
        vehicle.setId(id);
        Vehicle save = vehicleRepository.save(vehicle);
        VehicleInfoDTO vehicleInfoDTO = new VehicleInfoDTO(save);
        return vehicleInfoDTO;
    }

    public List<VehicleInfoDTO> getAll() {
        return vehicleRepository
                .findAll()
                .stream()
                .map(VehicleInfoDTO::new)
                .toList();
    }

    public Optional<VehicleInfoDTO> getById(Long id) {
        return vehicleRepository.findById(id).map(VehicleInfoDTO::new);
    }

    public void deleteVehicle(Long id) {
        Optional<Vehicle> byId = vehicleRepository.findById(id);
        if(byId.isEmpty()) throw new IllegalArgumentException("Vehicle Not found");
        vehicleRepository.delete(byId.get());
    }
}
