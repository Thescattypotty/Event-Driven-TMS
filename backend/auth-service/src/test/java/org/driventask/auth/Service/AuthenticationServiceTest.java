package org.driventask.auth.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.driventask.auth.FeignClient.UserClient;
import org.driventask.auth.Payload.Kafka.UserLogedIn;
import org.driventask.auth.Payload.Kafka.UserLogedOut;
import org.driventask.auth.Payload.Request.LoginRequest;
import org.driventask.auth.Payload.Request.UserAuthRequest;
import org.driventask.auth.Payload.Response.JwtResponse;
import org.driventask.auth.Payload.Response.UserAuthResponse;
import org.driventask.auth.Enum.ERole;
import org.driventask.auth.Exception.BadCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reactor.core.publisher.Mono;

@DisplayName("Test du service d'authentification")
@Feature("Service d'authentification")
public class AuthenticationServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthProducer authProducer;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Vérifier la connexion réussie de l'utilisateur")
    @Description("Vérifier la connexion réussie avec un email et un mot de passe valides")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur fournit des identifiants valides")
    public void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        UserAuthResponse userAuthResponse = new UserAuthResponse("test@example.com", Set.of(ERole.ROLE_USER));
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(userClient.verifyUserCredentials(any(UserAuthRequest.class))).thenReturn(userAuthResponse);
        when(jwtService.generateToken(any(String.class), any(Set.class), any(String.class))).thenReturn(accessToken, refreshToken);

        Mono<JwtResponse> result = authenticationService.login(loginRequest);

        result.subscribe(jwtResponse -> {
            assertEquals(accessToken, jwtResponse.accessToken());
            assertEquals(refreshToken, jwtResponse.refreshToken());
        });

        verify(authProducer, times(1)).handleUserAuthenticated(any(UserLogedIn.class));
    }

    @Test
    @DisplayName("Vérifier l'exception pour des identifiants invalides")
    @Description("Vérifier qu'une exception est levée pour des identifiants invalides")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur fournit des identifiants invalides")
    public void testLoginFailure() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrong-password");

        when(userClient.verifyUserCredentials(any(UserAuthRequest.class))).thenReturn(null);

        Mono<JwtResponse> result = authenticationService.login(loginRequest);

        result.subscribe(
            jwtResponse -> fail("Expected BadCredentialsException"),
            throwable -> assertTrue(throwable instanceof BadCredentialsException)
        );

        verify(authProducer, times(0)).handleUserAuthenticated(any(UserLogedIn.class));
    }

    @Test
    @Description("Vérifier qu'une exception est levée si l'email retourné est nul")
    @DisplayName("Vérifier l'exception pour un email nul")
    @Story("L'utilisateur ne fournit pas d'email")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithNullEmail() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        UserAuthResponse userAuthResponse = new UserAuthResponse(null, Set.of(ERole.ROLE_USER));

        when(userClient.verifyUserCredentials(any(UserAuthRequest.class))).thenReturn(userAuthResponse);

        Mono<JwtResponse> result = authenticationService.login(loginRequest);

        result.subscribe(
                jwtResponse -> fail("Expected BadCredentialsException"),
                throwable -> assertTrue(throwable instanceof BadCredentialsException));

        verify(authProducer, times(0)).handleUserAuthenticated(any(UserLogedIn.class));
    }

    @Test
    @DisplayName("Vérifier le comportement de déconnexion de l'utilisateur")
    @Description("Vérifier le comportement lorsqu'un utilisateur se déconnecte")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur se déconnecte")
    public void testLogout() {
        JwtResponse jwtResponse = new JwtResponse("access-token", "refresh-token");

        Mono<Void> result = authenticationService.logout(jwtResponse);

        result.subscribe();

        verify(authProducer, times(1)).handleUserLogout(any(UserLogedOut.class));
    }

    @Test
    @DisplayName("Vérifier l'exception pour un mot de passe vide")
    @Description("Vérifier qu'une exception est levée si le mot de passe est vide")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur ne fournit pas de mot de passe")
    public void testLoginWithEmptyPassword() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "");
        
        when(userClient.verifyUserCredentials(any(UserAuthRequest.class))).thenReturn(null);

        Mono<JwtResponse> result = authenticationService.login(loginRequest);

        result.subscribe(
            jwtResponse -> fail("Expected BadCredentialsException"),
            throwable -> assertTrue(throwable instanceof BadCredentialsException)
        );

        verify(authProducer, times(0)).handleUserAuthenticated(any(UserLogedIn.class));
    }

    @Test
    @DisplayName("Vérifier l'exception pour un format d'email invalide")
    @Description("Vérifier qu'une exception est levée pour un format d'email invalide")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur fournit un email avec un format invalide")
    public void testLoginWithInvalidEmailFormat() {
        LoginRequest loginRequest = new LoginRequest("invalid-email", "password");
        
        when(userClient.verifyUserCredentials(any(UserAuthRequest.class))).thenReturn(null);

        Mono<JwtResponse> result = authenticationService.login(loginRequest);

        result.subscribe(
            jwtResponse -> fail("Expected BadCredentialsException"),
            throwable -> assertTrue(throwable instanceof BadCredentialsException)
        );

        verify(authProducer, times(0)).handleUserAuthenticated(any(UserLogedIn.class));
    }
}