package hr.algebra.insurancebackend.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.dto.EmailRequestDTO;
import hr.algebra.insurancebackend.service.DriverService;
import hr.algebra.insurancebackend.exceptions.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static hr.algebra.insurancebackend.constants.ErrorMessages.DRIVER_NOT_FOUND_EMAIL;

@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final Counter createDriverFailureCounter;
    private final Timer getDriverByIdTimer;
    private final Timer getAllDriversTimer;
    private final Timer getDriverByVehicleTimer;
    private final Timer getDriverByEmailTimer;
    private final AtomicInteger associateDriverConcurrency;
    private final AtomicInteger disassociateDriverConcurrency;

    @PostMapping
    public DriverDTO createDriver(@RequestBody DriverDTO driverDTO) {
        try {
            return driverService.createDriver(driverDTO)
                    .orElseThrow(() -> {
                        createDriverFailureCounter.increment();
                        return new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong");
                    });
        } catch (ValidationException e) {
            createDriverFailureCounter.increment();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public DriverDTO getDriverById(@PathVariable Long id) {
        return getDriverByIdTimer.record(() ->
                driverService.getById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find driver with id: " + id))
        );
    }

    @GetMapping()
    public List<DriverDTO> getAllDrivers() {
        return getAllDriversTimer.record(() ->
                driverService.getAllDriversOfAuthenticatedVehicle()
        );
    }

    @GetMapping("/byVehicle")
    public List<DriverDTO> getDriverByVehicle(@RequestParam("plate") String plate) {
        return getDriverByVehicleTimer.record(() -> {
            try {
                return driverService.getByVehicle(plate);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        });
    }

    @GetMapping("/byEmail")
    public DriverDTO getDriverByEmail(@RequestParam("email") String email) {
        return getDriverByEmailTimer.record(() ->
                {
                    try {
                        return driverService.getByEmail(email)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email));
                    } catch (ValidationException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                    }
                }
        );
    }

    @PostMapping("/associate")
    public DriverDTO associateDriver(@RequestBody EmailRequestDTO email) {
        associateDriverConcurrency.incrementAndGet();
        try {
            return driverService.associateDriver(email.getEmail())
                    .orElseThrow(() -> {
                        associateDriverConcurrency.decrementAndGet();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email);
                    });
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } finally {
            associateDriverConcurrency.decrementAndGet();
        }
    }

    @DeleteMapping("/disassociate")
    public DriverDTO disassociateDriver(@RequestBody EmailRequestDTO email) {
        disassociateDriverConcurrency.incrementAndGet();
        try {
            return driverService.disassociateDriver(email.getEmail())
                    .orElseThrow(() -> {
                        disassociateDriverConcurrency.decrementAndGet();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email);
                    });
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } finally {
            disassociateDriverConcurrency.decrementAndGet();
        }
    }
}
