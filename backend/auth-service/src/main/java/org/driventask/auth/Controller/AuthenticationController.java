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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public Mono<ResponseEntity<JwtResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest)
            .doOnSubscribe(sub -> log.info("Subscription started"))
            .doOnNext(jwtResponse -> log.info("Generated JWT Response: " + jwtResponse.accessToken()))
            .doOnError(e -> log.error("Error during login: " + e.getMessage()))
            .map(jwtResponse -> new ResponseEntity<>(jwtResponse, HttpStatus.OK))
            .doFinally(signal -> log.info("Processing completed with signal: " + signal));
    }

    //this function isn't terminated we will replace JwtResponse with some security context to get the user or we will leave it like it is who knows
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody JwtResponse jwtResponse){
        return authenticationService.logout(jwtResponse)
            .then(Mono.just(new ResponseEntity<>(HttpStatus.OK)));
    }
}
