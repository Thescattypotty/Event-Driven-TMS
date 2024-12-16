package org.driventask.user.Payload.Response;

import java.util.Set;

import org.driventask.user.Enum.ERole;

public record UserResponse(
    String id,
    String fullname,
    String email,
    Set<ERole> roles
) {
    
}
