package hr.algebra.insurancebackend.aspect;

import hr.algebra.insurancebackend.dto.DriverDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.repository.DriverRepository;
import hr.algebra.insurancebackend.validator.EmailValidator;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import java.util.Date;

@Aspect
@Component
public class DriverValidatorAspect {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private EmailValidator emailValidator;

    @Pointcut("execution(* hr.algebra.insurancebackend.service.DriverService.createDriver(..))")
    public void createDriverPointcut() {}

    @Before("createDriverPointcut() && args(driverDTO)")

    public void validateDriverDTO(DriverDTO driverDTO) throws ValidationException {
        if (driverDTO == null) {
            throw new ValidationException("DriverDTO object cannot be null");
        }
        if (driverRepository.findByEmail(driverDTO.getEmail()).isPresent())
            throw new ValidationException("Driver with this email already exists");
        if (driverDTO.getName() == null || driverDTO.getName().isBlank()) {
            throw new ValidationException("Driver's name cannot be empty");
        }
        if (driverDTO.getName().length() > 50) {
            throw new ValidationException("Driver's name cannot exceed 50 characters");
        }
        if (driverDTO.getSurnames() == null || driverDTO.getSurnames().isBlank()) {
            throw new ValidationException("Driver's surnames cannot be empty");
        }
        if (driverDTO.getSurnames().length() > 100) {
            throw new ValidationException("Driver's surnames cannot exceed 100 characters");
        }
        if (driverDTO.getPassport() == null || driverDTO.getPassport().isBlank()) {
            throw new ValidationException("Driver's passport number cannot be empty");
        }
        if (driverDTO.getEmail() == null || driverDTO.getEmail().isBlank()) {
            throw new ValidationException("Driver's email cannot be empty");
        }
        if (!emailValidator.isValid(driverDTO.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        if (driverDTO.getBirthday() == null) {
            throw new ValidationException("Driver's birthday cannot be null");
        }
        if (driverDTO.getBirthday().after(new Date())) {
            throw new ValidationException("Driver's birthday cannot be in the future");
        }

    }
}

