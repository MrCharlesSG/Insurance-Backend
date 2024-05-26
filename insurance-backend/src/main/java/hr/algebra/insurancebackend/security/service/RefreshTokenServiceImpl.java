package hr.algebra.insurancebackend.security.service;

import hr.algebra.insurancebackend.security.domain.RefreshToken;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.security.dto.RefreshTokenRequestDTO;
import hr.algebra.insurancebackend.security.repository.RefreshTokenRepository;
import hr.algebra.insurancebackend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static hr.algebra.insurancebackend.security.configuration.TokenConfig.EXPIRATION_TIME_TOKEN;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    public RefreshToken createRefreshToken(String username) {
        Optional<UserInfo> userInfo = userRepository.findByUsername(username);
        if (userInfo.isEmpty()) throw new IllegalArgumentException("User Does not exists");
        Optional<RefreshToken> byUserInfoId = refreshTokenRepository.findByUserInfoId(userInfo.get().getId());


        RefreshToken refreshToken = RefreshToken.builder()
                .id(
                        byUserInfoId.map(RefreshToken::getId).orElse(0)
                )
                .userInfo(userInfo.get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(EXPIRATION_TIME_TOKEN))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

    @Override
    public JwtResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) throws IllegalAccessException {
        return findByToken(refreshTokenRequestDTO.getToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken())
                            .build();
                }).orElseThrow(() -> new IllegalAccessException("Refresh Token is not in DB..!!"));
    }

    @Override
    public JwtResponseDTO createRefreshTokenAndToken(String username) {
        RefreshToken refreshToken = createRefreshToken(username);
        return JwtResponseDTO.builder()
                .accessToken(jwtService.generateToken(username))
                .token(refreshToken.getToken())
                .build();
    }

}
