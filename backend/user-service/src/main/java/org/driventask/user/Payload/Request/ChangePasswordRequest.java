package org.driventask.user.Payload.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(
    @NotBlank
    @NotNull
    String oldPassword, 
    @NotBlank
    @NotNull
    String newPassword
) {
    
}
