package org.driventask.auth.IService;

import org.driventask.auth.Payload.Request.LoginRequest;
import org.driventask.auth.Payload.Response.JwtResponse;

import reactor.core.publisher.Mono;

public interface IAuthenticationService {
    Mono<JwtResponse> login(LoginRequest loginRequest);
    Mono<Void> logout(JwtResponse jwtResponse);
}
