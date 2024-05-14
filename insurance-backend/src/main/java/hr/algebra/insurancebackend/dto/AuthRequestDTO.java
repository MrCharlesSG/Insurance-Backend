package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.provider.UsernameProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequestDTO implements UsernameProvider {
    private String username;
    private String password;
}
