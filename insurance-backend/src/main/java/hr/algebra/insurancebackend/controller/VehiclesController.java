package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.security.service.AuthService;
import hr.algebra.insurancebackend.service.VehicleService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
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
    private JmsTemplate jmsTemplate;

    @PostMapping
    public VehicleInfoDTO createVehicle(@RequestBody VehicleCommand vehicleCommand){
        VehicleInfoDTO wrapped = null;
        /*
        jmsTemplate.convertAndSend(
                "Saving a vehicle in the database: " +
                        vehicleCommand
        );
         */

        try {
            wrapped = authService.registerVehicle(
                    SignUpVehicleDTO
                            .builder()
                            .vehicle(new VehicleInfoDTO(vehicleCommand))
                            .username(vehicleCommand.getPlate())
                            .password("user")
                            .matchingPassword("user")
                            .build()
            ).getWrapped();
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return wrapped;

    }

    @GetMapping("/{id}")
    public VehicleInfoDTO getVehicleById(@PathVariable Long id){
        return vehicleService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with id :" +id));
    }
    @GetMapping("/byPlate")
    public VehicleInfoDTO getVehicleByPlate(@PathParam("plate") String plate){
        return vehicleService.getByPlate(plate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with plate: " +plate));
    }
    @GetMapping()
    public List<VehicleInfoDTO> getAll(){
        return vehicleService.getAll();
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
