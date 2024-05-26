package hr.algebra.insurancebackend.security.aspect;

import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SignUpVehicleValidatorAspect {

    @Pointcut("execution(* hr.algebra.insurancebackend.security.service.AuthService.registerVehicle(..))")
    public void registerVehicleMethodPointcut() {}

    @Before("registerVehicleMethodPointcut() && args(signUpVehicleDTO)")
    public void validateSignUpVehicleDTO(SignUpVehicleDTO signUpVehicleDTO) throws ValidationException {
        if (signUpVehicleDTO == null) {
            throw new ValidationException("SignUpVehicleDTO object cannot be null");
        }
        if (signUpVehicleDTO.getUsername() == null || signUpVehicleDTO.getUsername().isBlank()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (signUpVehicleDTO.getPassword() == null || signUpVehicleDTO.getPassword().isBlank()) {
            throw new ValidationException("Password cannot be empty");
        }
        if (signUpVehicleDTO.getMatchingPassword() == null || signUpVehicleDTO.getMatchingPassword().isBlank()) {
            throw new ValidationException("Matching password cannot be empty");
        }
        if (!signUpVehicleDTO.getPassword().equals(signUpVehicleDTO.getMatchingPassword())) {
            throw new ValidationException("Passwords do not match");
        }
        if (signUpVehicleDTO.getVehicle() == null) {
            throw new ValidationException("Vehicle information cannot be null");
        }
    }
}

