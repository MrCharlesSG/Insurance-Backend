package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.service.DriverService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {

    private DriverService driverService;

    @PostMapping
    public DriverDTO createDriver(@RequestBody DriverDTO driverDTO){
        return driverService.createDriver(driverDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong"));
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable Long id){
        return driverService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with id :" +id));
    }

    @GetMapping()
    public List<DriverDTO> getAllDrivers(){
        List<DriverDTO> allDriversOfAuthenticatedVehicle = driverService.getAllDriversOfAuthenticatedVehicle();
        return allDriversOfAuthenticatedVehicle;
    }

    @PostMapping("/associate")
    public DriverDTO associateDriver(@PathParam("email") String email){
        DriverDTO driverDTO = driverService.associateDriver(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with email :" + email));
        return driverDTO;
    }
    @DeleteMapping("/disassociate")
    public DriverDTO disassociateDriver(@PathParam("email") String email){
        DriverDTO driverDTO = driverService.disassociateDriver(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with email :" + email));
        return driverDTO;
    }


}
