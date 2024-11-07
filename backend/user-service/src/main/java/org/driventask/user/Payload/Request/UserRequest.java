package org.driventask.user.Payload.Request;

import java.util.Set;

import org.driventask.user.Enum.ERole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
    @NotNull
    @NotBlank
    String fullname,
    @NotNull
    @Email
    String email,
    @NotNull
    @NotBlank
    String password,
    Set<ERole> roles
) {
    
}
