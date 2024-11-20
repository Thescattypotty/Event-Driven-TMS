package org.driventask.auth.Payload.Response;

import java.util.Set;

import org.driventask.auth.Enum.ERole;

public record UserAuthResponse(
    String email,
    Set<ERole> roles
) {
    
}
