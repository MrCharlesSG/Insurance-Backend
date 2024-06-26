package hr.algebra.insurancebackend.security.service;

import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.dto.AuthRequestDTO;
import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.security.repository.UserRepository;
import hr.algebra.insurancebackend.service.RegisterUserService;
import hr.algebra.insurancebackend.service.VehicleService;
import hr.algebra.insurancebackend.wrapper.AccessTokenWrapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private RegisterUserService registerUserService;

    @Lazy
    private VehicleService vehicleService;
    private RefreshTokenService refreshTokenService;


    @Override
    public JwtResponseDTO login(AuthRequestDTO authRequestDTO) throws ValidationException {
        try {
            Authentication authenticationUsername = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            if (authenticationUsername.isAuthenticated()) {
                return refreshTokenService.createRefreshTokenAndToken(authRequestDTO.getUsername());
            } else {
                throw new UsernameNotFoundException("invalid user request..!!");
            }
        }catch (AuthenticationException e){
            throw new UsernameNotFoundException("invalid user request..!!");
        }

    }

    @Override
    public AccessTokenWrapper<VehicleInfoDTO> registerVehicle(SignUpVehicleDTO signUpVehicleDTO) throws ValidationException {
        try {
            if (signUpVehicleDTO.getVehicle() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is necessary");

            UserInfo userInfo = registerUserService.registerNewUserAccount(signUpVehicleDTO);
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

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    @Override
    public UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String name = authentication.getName();
            Optional<UserInfo> userInfo = userRepository.findByUsername(name);
            return userInfo.orElse(null);
        }
        throw new UsernameNotFoundException("There is no user authenticated");
    }
}
