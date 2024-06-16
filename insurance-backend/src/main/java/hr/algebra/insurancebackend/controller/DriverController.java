package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.dto.EmailRequestDTO;
import hr.algebra.insurancebackend.service.DriverService;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static hr.algebra.insurancebackend.constants.ErrorMessages.DRIVER_NOT_FOUND_EMAIL;

@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {

    private DriverService driverService;

    @PostMapping
    public DriverDTO createDriver(@RequestBody DriverDTO driverDTO) {
        try {
            return driverService.createDriver(driverDTO)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong"));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public DriverDTO getDriverById(@PathVariable Long id) {
        return driverService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not found driver with id :" + id));
    }
    @GetMapping()
    public List<DriverDTO> getAllDrivers() {
        return driverService.getAllDriversOfAuthenticatedVehicle();
    }

    @GetMapping("/byVehicle")
    public List<DriverDTO> getDriverByVehicle(@PathParam("plate") String plate) {
        try {
            return driverService.getByVehicle(plate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/byEmail")
    public DriverDTO getDriverByEmail(@PathParam("email") String email) {
        try {
            return driverService.getByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PostMapping("/associate")
    public DriverDTO associateDriver(@RequestBody EmailRequestDTO email) {
        DriverDTO driverDTO = null;
        try {
            driverDTO = driverService.associateDriver(email.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return driverDTO;
    }

    @DeleteMapping("/disassociate")
    public DriverDTO disassociateDriver(@RequestBody EmailRequestDTO email) {
        DriverDTO driverDTO = null;
        try {
            driverDTO = driverService.disassociateDriver(email.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email));
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return driverDTO;
    }
}
