package org.driventask.auth.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.driventask.auth.Payload.Request.LoginRequest;
import org.driventask.auth.Payload.Response.JwtResponse;
import org.driventask.auth.Service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reactor.core.publisher.Mono;

@DisplayName("Authentication Controller Test")
@Feature("Authentication Controller")
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    
    @Test
    @DisplayName("Test de connexion")
    @Description("Test de connexion avec un email et un mot de passe valides")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Test de connexion")
    public void testLogin() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        JwtResponse jwtResponse = new JwtResponse("access-token", "refresh-token");

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(Mono.just(jwtResponse));

        Mono<ResponseEntity<JwtResponse>> result = authenticationController.login(loginRequest);

        result.subscribe(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(jwtResponse, response.getBody());
        });

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Test de déconnexion")
    @Description("Test de déconnexion avec un token d'accès et un token de rafraîchissement valides")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Test de déconnexion")
    public void testLogout() {
        JwtResponse jwtResponse = new JwtResponse("access-token", "refresh-token");

        when(authenticationService.logout(any(JwtResponse.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> result = authenticationController.logout(jwtResponse);

        result.subscribe(response -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
        });

        verify(authenticationService, times(1)).logout(any(JwtResponse.class));
    }
}