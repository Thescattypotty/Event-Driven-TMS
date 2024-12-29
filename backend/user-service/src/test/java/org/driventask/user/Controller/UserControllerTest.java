package org.driventask.user.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

import java.util.Set;
import java.util.UUID;

import org.driventask.user.Enum.ERole;
import org.driventask.user.Payload.Request.UserAuthRequest;
import org.driventask.user.Payload.Request.UserRequest;
import org.driventask.user.Payload.Response.UserAuthResponse;
import org.driventask.user.Payload.Response.UserResponse;
import org.driventask.user.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@DisplayName("User Controller Test")
@Feature("User Management")
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = bindToController(userController).build();
    }

    @Test
    @Feature("User Management")
    @Story("Get User by ID")
    @DisplayName("Get User by ID - Success")
    @Description("Test for successfully retrieving a user by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserByIdSuccess() {
        UserResponse userResponse = new UserResponse(null, null, "test@example.com", null);

        when(userService.getUser(any(String.class))).thenReturn(Mono.just(userResponse));

        webTestClient.get().uri("/api/v1/user/{id}", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponse.class)
            .value(response -> {
                assertEquals("test@example.com", response.email());
            });
    }

    @Test
    @Feature("User Management")
    @Story("Get User by Email")
    @DisplayName("Get User by Email - Success")
    @Description("Test for successfully retrieving a user by email")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserByEmailSuccess() {
        UserResponse userResponse = new UserResponse(null, null, "test@example.com", null);

        when(userService.getUserByEmail(any(String.class))).thenReturn(Mono.just(userResponse));

        webTestClient.get().uri("/api/v1/user/email/{email}", "test@example.com")
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponse.class)
            .value(response -> {
                assertEquals("test@example.com", response.email());
            });
    }

    @Test
    @Feature("User Management")
    @Story("Check if User Exists")
    @DisplayName("Check if User Exists - Success")
    @Description("Test for successfully checking if a user exists")
    @Severity(SeverityLevel.CRITICAL)
    public void testIsUserExistSuccess() {
        when(userService.isUserExist(any(String.class))).thenReturn(Mono.just(true));

        webTestClient.get().uri("/api/v1/user/exists/{id}", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isAccepted()
            .expectBody(Boolean.class)
            .value(response -> {
                assertTrue(response);
            });
    }

    @Test
    @Feature("User Management")
    @Story("Create User")
    @DisplayName("Create User - Success")
    @Description("Test for successfully creating a user")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserSuccess() {
        UserRequest userRequest = new UserRequest("lala", "test@exemple.com", "password", null);

        when(userService.createUser(any(UserRequest.class))).thenReturn(Mono.empty());

        webTestClient.post().uri("/api/v1/user")
            .bodyValue(userRequest)
            .exchange()
            .expectStatus().isCreated();
    }

    @Test
    @Feature("User Management")
    @Story("Update User")
    @DisplayName("Update User - Success")
    @Description("Test for successfully updating a user")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUserSuccess() {
        UserRequest userRequest = new UserRequest("lala", "test@exemple.com", "password", null);

        when(userService.updateUser(any(String.class), any(UserRequest.class))).thenReturn(Mono.empty());

        webTestClient.put().uri("/api/v1/user/{id}", UUID.randomUUID().toString())
            .bodyValue(userRequest)
            .exchange()
            .expectStatus().isAccepted();
    }

    @Test
    @Feature("User Management")
    @Story("Delete User")
    @DisplayName("Delete User - Success")
    @Description("Test for successfully deleting a user")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUserSuccess() {
        when(userService.deleteUser(any(String.class))).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/user/{id}", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isAccepted();
    }

    @Test
    @Feature("User Management")
    @Story("Verify User Credentials")
    @DisplayName("Verify User Credentials - Success")
    @Description("Test for successfully verifying user credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testVerifyUserCredentialsSuccess() {
        UserAuthRequest userAuthRequest = new UserAuthRequest("test@example.com", "password");
        UserAuthResponse userAuthResponse = new UserAuthResponse("test@example.com", Set.of(ERole.ROLE_USER));

        when(userService.verifyUserCredentials(any(UserAuthRequest.class))).thenReturn(Mono.just(userAuthResponse));

        webTestClient.post().uri("/api/v1/user/auth/verify")
            .bodyValue(userAuthRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserAuthResponse.class)
            .value(response -> {
                assertEquals("test@example.com", response.email());
                assertEquals(Set.of(ERole.ROLE_USER), response.roles());
            });
    }

    @Test
    @Feature("User Management")
    @Story("Get All Users")
    @DisplayName("Get All Users - Success")
    @Description("Test for successfully retrieving all users")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllUsersSuccess() {
        UserResponse userResponse = new UserResponse(null, null, "test@example.com", null);

        when(userService.getAllUsers()).thenReturn(Flux.just(userResponse));

        webTestClient.get().uri("/api/v1/user")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserResponse.class)
            .value(response -> {
                assertEquals(1, response.size());
                assertEquals("test@example.com", response.get(0).email());
            });
    }
}