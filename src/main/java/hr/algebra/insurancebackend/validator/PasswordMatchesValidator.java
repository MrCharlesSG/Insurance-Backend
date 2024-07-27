package hr.algebra.insurancebackend.validator;


import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        SignUpVehicleDTO user = (SignUpVehicleDTO) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
