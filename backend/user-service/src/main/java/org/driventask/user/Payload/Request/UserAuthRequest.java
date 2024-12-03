package org.driventask.user.Payload.Request;

public record UserAuthRequest(
    String email,
    String password
) {
    
}

