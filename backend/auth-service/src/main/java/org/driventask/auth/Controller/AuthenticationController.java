package org.driventask.auth.Controller;

import org.springframework.http.HttpStatus;
import org.driventask.auth.Payload.Request.LoginRequest;
import org.driventask.auth.Payload.Response.JwtResponse;
import org.driventask.auth.Service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public Mono<ResponseEntity<JwtResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return authenticationService.login(loginRequest)
            .map(jwtResponse -> new ResponseEntity<>(jwtResponse, HttpStatus.OK));
    }
}
