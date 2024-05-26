package hr.algebra.insurancebackend.security.controller;

import hr.algebra.insurancebackend.security.dto.AuthRequestDTO;
import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.security.dto.RefreshTokenRequestDTO;
import hr.algebra.insurancebackend.security.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.security.service.AuthService;
import hr.algebra.insurancebackend.security.service.RefreshTokenService;
import hr.algebra.insurancebackend.security.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static hr.algebra.insurancebackend.utils.JwtUtil.extractTokenFromRequest;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
@Validated
public class AuthController {

    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    private TokenBlackListService tokenBlacklist;

    @PostMapping("/api/v1/login")
    public Object authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        try{
            return authService.login(authRequestDTO);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/v1/register/vehicle")
    public Object registrationOfVehicleAndGetToken(@Valid @RequestBody SignUpVehicleDTO signUpVehicleDTO){
        try {
            return authService.registerVehicle(signUpVehicleDTO);
        } catch (hr.algebra.insurancebackend.exceptions.ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        tokenBlacklist.addToBlacklist(token);

        // Clear any session-related data if necessary

        return ResponseEntity.ok("Logged out successfully");
    }



    @PostMapping("/api/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        try {
            return  refreshTokenService.refreshToken(refreshTokenRequestDTO);
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
