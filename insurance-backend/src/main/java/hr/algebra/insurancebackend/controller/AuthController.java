package hr.algebra.insurancebackend.controller;

import hr.algebra.insurancebackend.dto.AuthRequestDTO;
import hr.algebra.insurancebackend.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.dto.RefreshTokenRequestDTO;
import hr.algebra.insurancebackend.dto.SignUpVehicleDTO;
import hr.algebra.insurancebackend.service.AuthService;
import hr.algebra.insurancebackend.service.RefreshTokenService;
import hr.algebra.insurancebackend.service.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return authService.login(authRequestDTO);
    }

    @PostMapping("/api/v1/register/vehicle")
    public Object registrationOfVehicleAndGetToken(@Valid @RequestBody SignUpVehicleDTO signUpVehicleDTO){
        return authService.registerVehicle(signUpVehicleDTO);
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
        return  refreshTokenService.refreshToken(refreshTokenRequestDTO);
    }
}
