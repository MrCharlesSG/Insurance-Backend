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

    public Optional<VehicleInfoDTO> createVehicle(VehicleCommand vehicleCommand) {
        /*UserInfo register = authService.registerVehicle(vehicleCommand.getUser());
        if(register!=null){
            return Optional.of(
                    new VehicleInfoDTO(
                          vehicleRepository.save(
                                  new Vehicle(
                                          vehicleCommand,
                                          register
                                  )
                          )
                    )
            );
        }

         */
        return createVehicle(new VehicleInfoDTO(vehicleCommand), null);
    }

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


    public Object update(Long id, VehicleCommand vehicleCommand) {
        return null;
    }

    public List<VehicleInfoDTO> getAll() {
        return Collections.emptyList();
    }

    public Optional<VehicleInfoDTO> getById(Long id) {
        return Optional.empty();
    }

    public void deleteVehicle(Long id) {
        
    }
}
