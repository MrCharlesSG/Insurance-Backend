package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.dto.VehicleDTO;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.security.service.AuthService;
import hr.algebra.insurancebackend.service.VehicleService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/*
Controller that will be used for mule
 */
@RestController
@RequestMapping("vehicles")
@AllArgsConstructor
public class VehiclesController {

    private VehicleService vehicleService;
    private AuthService authService;

    @PostMapping
    public VehicleInfoDTO createVehicle(@RequestBody VehicleCommand vehicleCommand){
        VehicleInfoDTO wrapped = authService.registerVehicle(
                SignUpVehicleDTO
                        .builder()
                        .vehicle(new VehicleInfoDTO(vehicleCommand))
                        .username(vehicleCommand.getPlate())
                        .password("user")
                        .matchingPassword("user")
                        .build()
        ).getWrapped();
        return wrapped;

    }

    @GetMapping("/{id}")
    public VehicleInfoDTO getDriverById(@PathVariable Long id){
        return vehicleService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with id :" +id));
    }
    @GetMapping()
    public List<VehicleInfoDTO> getAll(){
        return vehicleService.getAll();
                //.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with email :" +email));
    }

    @PutMapping("/{id}")
    public VehicleInfoDTO updateVehicle(@PathVariable Long id,@RequestBody VehicleCommand vehicleCommand){
        return vehicleService.update(id,
                vehicleCommand);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id){
        try {
            vehicleService.deleteVehicle(id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
