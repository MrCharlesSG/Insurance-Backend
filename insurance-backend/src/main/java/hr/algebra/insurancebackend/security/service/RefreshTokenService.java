package hr.algebra.insurancebackend.security.service;

import hr.algebra.insurancebackend.security.domain.RefreshToken;
import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.security.dto.RefreshTokenRequestDTO;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    JwtResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) throws IllegalAccessException;

    JwtResponseDTO createRefreshTokenAndToken(String username);
}
