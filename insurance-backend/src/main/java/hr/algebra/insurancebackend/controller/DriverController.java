package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {

    private DriverService driverService;

    @PostMapping
    public Object createDriver(@RequestBody DriverDTO driverDTO){
        return driverService.createDriver(driverDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong"));
    }

}
