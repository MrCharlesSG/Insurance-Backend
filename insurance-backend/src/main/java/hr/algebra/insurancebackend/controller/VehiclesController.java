package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
