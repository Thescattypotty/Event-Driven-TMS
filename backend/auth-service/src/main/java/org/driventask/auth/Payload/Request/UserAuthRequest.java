package org.driventask.auth.Payload.Request;

public record UserAuthRequest(
    String email,
    String password
) {
    
}
