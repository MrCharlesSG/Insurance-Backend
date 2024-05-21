package hr.algebra.insurancebackend.security.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface SignUpDTO {

    String getUsername();
    String getPassword();
    String getMatchingPassword();
}
