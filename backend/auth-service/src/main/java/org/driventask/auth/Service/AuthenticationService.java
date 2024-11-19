package org.driventask.auth.Service;

import org.driventask.auth.FeignClient.UserClient;
import org.driventask.auth.IService.IAuthenticationService;
import org.driventask.auth.Payload.Request.LoginRequest;
import org.driventask.auth.Payload.Request.UserAuthRequest;
import org.driventask.auth.Payload.Response.JwtResponse;
import org.springframework.stereotype.Service;
import org.driventask.auth.Exception.BadCredentialsException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService{

    private final UserClient userClient;
    private final JwtService jwtService;

    @Override
    public Mono<JwtResponse> login(LoginRequest loginRequest) {
        return userClient
            .verifyUserCredentials(new UserAuthRequest(loginRequest.email(), loginRequest.password()))
            .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid Email or Password .")))
            .flatMap(userAuthResponse -> {
                String accessToken = jwtService.generateToken(userAuthResponse.email(), userAuthResponse.roles(), "ACCESS");
                String refreshToken = jwtService.generateToken(userAuthResponse.email(), userAuthResponse.roles(), "REFRESH");
                return Mono.just(new JwtResponse(accessToken, refreshToken));
                }
            )
            .onErrorMap(e -> new BadCredentialsException("Invalid email or password .", e));
    }
    
}
