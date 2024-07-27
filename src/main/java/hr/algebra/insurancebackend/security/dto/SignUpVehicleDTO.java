package hr.algebra.insurancebackend.security.dto;

import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.validator.PasswordMatches;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
@Builder
public class SignUpVehicleDTO implements SignUpDTO {
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private VehicleInfoDTO vehicle;
}
