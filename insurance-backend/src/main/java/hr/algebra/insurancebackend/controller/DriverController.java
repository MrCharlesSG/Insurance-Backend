package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.domain.Driver;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.service.DriverService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        try {
            return driverService.createDriver(driverDTO)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong"));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable Long id){
        return driverService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with id :" +id));
    }
    @GetMapping("/byEmail")
    public Driver getDriverById(@PathParam("email") String email){
        try {
            return driverService.getByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with email :" +email));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    @GetMapping("/byVehicle")
    public List<DriverDTO> getDriverByVehicle(@PathParam("plate") String plate){
        try{
            return driverService.getByVehicle(plate);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping()
    public List<DriverDTO> getAllDrivers(){
        return driverService.getAllDriversOfAuthenticatedVehicle();
    }

    @PostMapping("/associate")
    public DriverDTO associateDriver(@PathParam("email") String email){
        DriverDTO driverDTO = null;
        try {
            driverDTO = driverService.associateDriver(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with email :" + email));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return driverDTO;
    }
    @DeleteMapping("/disassociate")
    public DriverDTO disassociateDriver(@PathParam("email") String email){
        DriverDTO driverDTO = null;
        try {
            driverDTO = driverService.disassociateDriver(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with email :" + email));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return driverDTO;
    }


}
