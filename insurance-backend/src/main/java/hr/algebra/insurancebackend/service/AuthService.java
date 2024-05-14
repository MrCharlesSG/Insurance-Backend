package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.domain.UserInfo;
import hr.algebra.insurancebackend.dto.AuthRequestDTO;
import hr.algebra.insurancebackend.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.repository.UserRepository;
import hr.algebra.insurancebackend.wrapper.AccessTokenWrapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private UserService userService;
    private VehicleService vehicleService;
    private RefreshTokenService refreshTokenService;


    public JwtResponseDTO login(AuthRequestDTO authRequestDTO) {
        Authentication authenticationUsername = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (authenticationUsername.isAuthenticated()) {
            return refreshTokenService.createRefreshTokenAndGenerateResponse(authRequestDTO.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }

    public AccessTokenWrapper<VehicleInfoDTO> registerVehicle(SignUpVehicleDTO signUpVehicleDTO) {
        try {
            if (signUpVehicleDTO.getVehicle() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is necessary");

            UserInfo userInfo = userService.registerNewUserAccount(signUpVehicleDTO);
            Optional<VehicleInfoDTO> vehicleInfoDTOOptional = vehicleService.createVehicle(signUpVehicleDTO.getVehicle(), userInfo);

            if (vehicleInfoDTOOptional.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create vehicle");

            VehicleInfoDTO vehicleInfoDTO = vehicleInfoDTOOptional.get();
            vehicleInfoDTO.setPlate(userInfo.getUsername());

            return AccessTokenWrapper.
                    <VehicleInfoDTO>builder()
                    .wrapped(vehicleInfoDTO)
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User was not found"));
        }
        throw new UsernameNotFoundException("There is no user authenticated");
    }
}
