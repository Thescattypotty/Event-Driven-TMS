package org.driventask.user.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.driventask.user.Entity.User;
import org.driventask.user.Enum.ERole;
import org.driventask.user.Exception.PasswordIncorrectException;
import org.driventask.user.Exception.UserNotFoundException;
import org.driventask.user.Payload.Mapper.UserMapper;
import org.driventask.user.Payload.Request.ChangePasswordRequest;
import org.driventask.user.Payload.Request.UserAuthRequest;
import org.driventask.user.Payload.Response.UserAuthResponse;
import org.driventask.user.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@DisplayName("Test pour UserService")
@Feature("User Service")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Vérifier les informations d'identification de l'utilisateur avec succès")
    @Description("Test pour vérifier les informations d'identification de l'utilisateur avec succès")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Vérification des informations d'identification de l'utilisateur")
    public void testVerifyUserCredentialsSuccess() {
        UserAuthRequest userAuthRequest = new UserAuthRequest("test@example.com", "password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(ERole.ROLE_USER));

        when(userRepository.findByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        Mono<UserAuthResponse> result = userService.verifyUserCredentials(userAuthRequest);

        result.subscribe(userAuthResponse -> {
            assertEquals("test@example.com", userAuthResponse.email());
            assertEquals(Set.of("ROLE_USER"), userAuthResponse.roles());
        });
    }

    @Test
    @DisplayName("Vérifier les informations d'identification de l'utilisateur - utilisateur non trouvé")
    @Description("Test pour vérifier les informations d'identification de l'utilisateur lorsque l'utilisateur n'est pas trouvé")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Vérification des informations d'identification de l'utilisateur")
    public void testVerifyUserCredentialsUserNotFound() {
        UserAuthRequest userAuthRequest = new UserAuthRequest("test@example.com", "password");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Mono.empty());

        Mono<UserAuthResponse> result = userService.verifyUserCredentials(userAuthRequest);

        result.subscribe(
            userAuthResponse -> fail("Expected UserNotFoundException"),
            throwable -> assertTrue(throwable instanceof UserNotFoundException)
        );
    }

    @Test
    @DisplayName("Vérifier les informations d'identification de l'utilisateur - mot de passe incorrect")
    @Description("Test pour vérifier les informations d'identification de l'utilisateur lorsque le mot de passe est incorrect")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Vérification des informations d'identification de l'utilisateur")
    public void testVerifyUserCredentialsIncorrectPassword() {
        UserAuthRequest userAuthRequest = new UserAuthRequest("test@example.com", "wrong-password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        Mono<UserAuthResponse> result = userService.verifyUserCredentials(userAuthRequest);

        result.subscribe(
            userAuthResponse -> fail("Expected PasswordIncorrectException"),
            throwable -> assertTrue(throwable instanceof PasswordIncorrectException)
        );
    }

    @Test
    @DisplayName("Obtenir l'utilisateur par email avec succès")
    @Description("Test pour obtenir l'utilisateur par email avec succès")
    @Severity(SeverityLevel.NORMAL)
    @Story("Obtenir l'utilisateur par email")
    void testGetUserByEmailSuccess() {
        User user = new User();
        user.setEmail("email@example.com");
        when(userRepository.findByEmail("email@example.com")).thenReturn(Mono.just(user));

        userService.getUserByEmail("email@example.com")
            .subscribe(result -> assertEquals("email@example.com", result.email()));
    }

    @Test
    @DisplayName("Obtenir l'utilisateur par email - utilisateur non trouvé")
    @Description("Test pour obtenir l'utilisateur par email lorsque l'utilisateur n'est pas trouvé")
    @Severity(SeverityLevel.NORMAL)
    @Story("Obtenir l'utilisateur par email")
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("email@example.com")).thenReturn(Mono.empty());

        userService.getUserByEmail("email@example.com")
            .subscribe(
                result -> fail("Should throw UserNotFoundException"),
                ex -> assertTrue(ex instanceof UserNotFoundException)
            );
    }

    @Test
    @DisplayName("Changer le mot de passe avec succès")
    @Description("Test pour changer le mot de passe avec succès")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Changer le mot de passe")
    void testChangePasswordSuccess() {
        User user = new User();
        user.setPassword("encodedOldPass");
        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        userService.changePassword(UUID.randomUUID().toString(), new ChangePasswordRequest("oldPass", "newPass"))
            .subscribe(
                unused -> assertTrue(true),
                ex -> fail("Should not fail")
            );
    }

    @Test
    @DisplayName("Changer le mot de passe - ancien mot de passe incorrect")
    @Description("Test pour changer le mot de passe lorsque l'ancien mot de passe est incorrect")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Changer le mot de passe")
    void testChangePasswordOldPasswordIncorrect() {
        User user = new User();
        user.setPassword("encodedOldPass");
        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

        userService.changePassword(UUID.randomUUID().toString(), new ChangePasswordRequest("wrongPass", "newPass"))
            .subscribe(
                unused -> fail("Should throw PasswordIncorrectException"),
                ex -> assertTrue(ex instanceof PasswordIncorrectException)
            );
    }

    @Test
    @DisplayName("Vérifier l'existence de l'utilisateur - utilisateur existant")
    @Description("Test pour vérifier l'existence de l'utilisateur lorsque l'utilisateur existe")
    @Severity(SeverityLevel.NORMAL)
    @Story("Vérifier l'existence de l'utilisateur")
    void testIsUserExist() {
        when(userRepository.existsById(any(UUID.class))).thenReturn(Mono.just(true));

        userService.isUserExist(UUID.randomUUID().toString())
            .subscribe(exists -> assertTrue(exists));
    }

    @Test
    @DisplayName("Vérifier l'existence de l'utilisateur - utilisateur non existant")
    @Description("Test pour vérifier l'existence de l'utilisateur lorsque l'utilisateur n'existe pas")
    @Severity(SeverityLevel.NORMAL)
    @Story("Vérifier l'existence de l'utilisateur")
    void testIsUserNotExist() {
        when(userRepository.existsById(any(UUID.class))).thenReturn(Mono.just(false));

        userService.isUserExist(UUID.randomUUID().toString())
            .subscribe(exists -> assertTrue(!exists));
    }

    @Test
    @DisplayName("Obtenir tous les utilisateurs - non vide")
    @Description("Test pour obtenir tous les utilisateurs lorsque la liste n'est pas vide")
    @Severity(SeverityLevel.NORMAL)
    @Story("Obtenir tous les utilisateurs")
    void testGetAllUsersNotEmpty() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(Flux.just(user));

        userService.getAllUsers()
            .collectList()
            .subscribe(users -> assertTrue(!users.isEmpty()));
    }

    @Test
    @DisplayName("Obtenir tous les utilisateurs - vide")
    @Description("Test pour obtenir tous les utilisateurs lorsque la liste est vide")
    @Severity(SeverityLevel.NORMAL)
    @Story("Obtenir tous les utilisateurs")
    void testGetAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        userService.getAllUsers()
            .collectList()
            .subscribe(users -> assertTrue(users.isEmpty()));
    }
}
