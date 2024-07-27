package hr.algebra.insurancebackend.wrapper;

import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccessTokenWrapper<T> {

    private T wrapped;

    private JwtResponseDTO token;

}
