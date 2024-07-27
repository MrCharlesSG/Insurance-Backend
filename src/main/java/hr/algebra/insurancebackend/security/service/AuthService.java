package hr.algebra.insurancebackend.security.service;

import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.dto.AuthRequestDTO;
import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.wrapper.AccessTokenWrapper;

public interface AuthService {
    JwtResponseDTO login(AuthRequestDTO authRequestDTO) throws ValidationException;

    AccessTokenWrapper<VehicleInfoDTO> registerVehicle(SignUpVehicleDTO signUpVehicleDTO) throws ValidationException;

    String getCurrentUsername();

    UserInfo getCurrentUser();
}
