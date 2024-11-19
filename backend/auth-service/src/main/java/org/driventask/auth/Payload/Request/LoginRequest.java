package org.driventask.auth.Payload.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull(message = "Email is Required")
    @Email(message ="Invalid Format of Email")
    String email,
    
    @NotNull(message = "Password is Required")
    @NotBlank(message = "Password cannot be blank")
    String password
) {
    
}
