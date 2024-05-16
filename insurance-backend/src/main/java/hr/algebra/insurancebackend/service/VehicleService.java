package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.UserInfo;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
        return Optional.empty();
    }

    public Optional<VehicleInfoDTO> createVehicle(VehicleInfoDTO vehicle, UserInfo userInfo) {
        return Optional.of(new VehicleInfoDTO(vehicleRepository.save(new Vehicle(vehicle, userInfo))));
    }

    public Optional<Vehicle> getVehicleOfUserInfo(UserInfo userInfo) {
        Optional<Vehicle> byUserInfoId = vehicleRepository.findByUserInfoId(userInfo.getId());
        return byUserInfoId;
    }

    public Optional<Vehicle> getAuthenticatedVehicle(){
        UserInfo currentUser = authService.getCurrentUser();
        return getVehicleOfUserInfo(currentUser);
    }


}
