package org.driventask.auth.Payload.Response;

public record JwtResponse(
    String accessToken,
    String refreshToken
) {
    
}
