package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.dto.VehicleDTO;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
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

    @PostMapping
    public Object createVehicle(@RequestBody VehicleCommand vehicleCommand){
        return vehicleService.createVehicle(vehicleCommand);
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
    public Object updateVehicle(@PathVariable Long id,@RequestBody VehicleCommand vehicleCommand){
        return vehicleService.update(id,
                vehicleCommand);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id){
        vehicleService.deleteVehicle(id);
    }
}
