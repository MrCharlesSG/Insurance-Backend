package hr.algebra.insurancebackend.security.aspect;

import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.security.dto.AuthRequestDTO;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthRequestValidatorAspect {

    @Pointcut("execution(* hr.algebra.insurancebackend.security.service.AuthService.login(..))")
    public void loginMethodPointcut() {}

    @Before("loginMethodPointcut() && args(authRequestDTO)")
    public void validateAuthRequestDTO(AuthRequestDTO authRequestDTO) throws ValidationException {
        if (authRequestDTO == null) {
            throw new ValidationException("AuthRequestDTO object cannot be null");
        }
        if (authRequestDTO.getUsername() == null || authRequestDTO.getUsername().isBlank()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (authRequestDTO.getPassword() == null || authRequestDTO.getPassword().isBlank()) {
            throw new ValidationException("Password cannot be empty");
        }
    }
}

