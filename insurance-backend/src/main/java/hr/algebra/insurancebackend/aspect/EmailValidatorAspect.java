package hr.algebra.insurancebackend.aspect;

import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.validator.EmailValidator;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EmailValidatorAspect {

    @Autowired
    private EmailValidator emailValidator;
    @Pointcut("execution(* hr.algebra.insurancebackend.service.DriverService.associateDriver(String)) && args(email)")
    public void associateDriverMethod(String email) {}

    @Pointcut("execution(* hr.algebra.insurancebackend.service.DriverService.disassociateDriver(String)) && args(email)")
    public void disassociateDriverMethod(String email) {}

    @Pointcut("execution(* hr.algebra.insurancebackend.service.DriverService.getByEmail(String)) && args(email)")
    public void getByEmailMethod(String email) {}

    @Pointcut("associateDriverMethod(email) || disassociateDriverMethod(email) || getByEmailMethod(email)")
    public void driverServiceEmailMethods(String email) {}

    @Before("driverServiceEmailMethods(email)")
    public void validateEmail(String email) throws ValidationException {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!emailValidator.isValid(email)) {
            throw new ValidationException("Invalid email format");
        }
    }
}
