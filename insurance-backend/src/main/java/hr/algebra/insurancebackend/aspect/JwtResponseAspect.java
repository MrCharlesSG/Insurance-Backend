package hr.algebra.insurancebackend.aspect;


import hr.algebra.insurancebackend.dto.JwtResponseDTO;
import hr.algebra.insurancebackend.provider.UsernameProvider;
import hr.algebra.insurancebackend.service.RefreshTokenService;
import hr.algebra.insurancebackend.wrapper.AccessTokenWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JwtResponseAspect {
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Around("execution(* hr.algebra.insurancebackend.controller.AuthController.registrationOfVehicleAndGetToken(..))")
    public AccessTokenWrapper authControllerRegisterMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        AccessTokenWrapper result = (AccessTokenWrapper) joinPoint.proceed();
        Object wrapped = result.getWrapped();
        if (wrapped instanceof UsernameProvider usernameProvider) {
            String username = usernameProvider.getUsername();
            JwtResponseDTO refreshTokenAndGenerateResponse = refreshTokenService.createRefreshTokenAndGenerateResponse(username);
            return AccessTokenWrapper
                    .builder()
                    .wrapped(wrapped)
                    .token(refreshTokenAndGenerateResponse)
                    .build();
        } else {
            throw new RuntimeException("Something went wrong in the server");
        }
    }
}

