package org.driventask.auth.Service;

import org.driventask.auth.FeignClient.UserClient;
import org.driventask.auth.IService.IAuthenticationService;
import org.driventask.auth.Payload.Kafka.UserLogedIn;
import org.driventask.auth.Payload.Kafka.UserLogedOut;
import org.driventask.auth.Payload.Request.LoginRequest;
import org.driventask.auth.Payload.Request.UserAuthRequest;
import org.driventask.auth.Payload.Response.JwtResponse;
import org.driventask.auth.Payload.Response.UserAuthResponse;
import org.springframework.stereotype.Service;
import org.driventask.auth.Exception.BadCredentialsException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService{

    private final UserClient userClient;
    private final JwtService jwtService;
    private final AuthProducer authProducer;

    @Override
    public Mono<JwtResponse> login(LoginRequest loginRequest) {
        return Mono.fromCallable(() -> {
                UserAuthResponse userAuthResponse = userClient.verifyUserCredentials(
                    new UserAuthRequest(loginRequest.email(), loginRequest.password())
                );

                if (userAuthResponse == null || userAuthResponse.email() == null) {
                    throw new BadCredentialsException("Invalid Email or Password.");
                }

                String accessToken = jwtService.generateToken(userAuthResponse.email(), userAuthResponse.roles(), "ACCESS");
                String refreshToken = jwtService.generateToken(userAuthResponse.email(), userAuthResponse.roles(), "REFRESH");
                System.out.println("Access Token :" + accessToken);
                System.out.println("Refresh Token :" + refreshToken);
                //authProducer.handleUserAuthenticated(new UserLogedIn(accessToken));

                return new JwtResponse(accessToken, refreshToken);
            }
        )
        .doOnSuccess(jwtResponse -> authProducer.handleUserAuthenticated(new UserLogedIn(jwtResponse.accessToken())))
        .onErrorMap(e -> {
                System.out.println("Error occurred: " + e.getMessage());
                return new BadCredentialsException("Invalid email or password.", e);
                });
    }

    @Override
    public Mono<Void> logout(JwtResponse jwtResponse) {
        authProducer.handleUserLogout(new UserLogedOut(jwtResponse.accessToken()));
        return Mono.empty();
    }
    
}
