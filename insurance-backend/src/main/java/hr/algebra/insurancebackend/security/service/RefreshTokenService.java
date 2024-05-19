package hr.algebra.insurancebackend.security.service;

import hr.algebra.insurancebackend.security.domain.RefreshToken;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.security.dto.RefreshTokenRequestDTO;
import hr.algebra.insurancebackend.security.repository.RefreshTokenRepository;
import hr.algebra.insurancebackend.security.repository.UserRepository;
import hr.algebra.insurancebackend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    public RefreshToken createRefreshToken(String username){
        Optional<UserInfo> userInfo = userRepository.findByUsername(username);
        if(userInfo.isEmpty()) throw new IllegalArgumentException("User Does not exists");
        Optional<RefreshToken> refreshTokenInDatabaseOptional = refreshTokenRepository.findByUserInfoIdAndExpiryDateAfter(userInfo.get().getId(), Instant.now());
        RefreshToken refreshToken;
        refreshToken = refreshTokenInDatabaseOptional.orElseGet(() -> RefreshToken.builder()
                .userInfo(userInfo.get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(60000)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build());
        return refreshTokenRepository.save(refreshToken);
    }



    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

    public JwtResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO){
        return findByToken(refreshTokenRequestDTO.getToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }

    public JwtResponseDTO createRefreshTokenAndGenerateResponse(String username) {
        RefreshToken refreshToken = createRefreshToken(username);
        return JwtResponseDTO.builder()
                .accessToken(jwtService.generateToken(username))
                .token(refreshToken.getToken())
                .build();
    }

}
