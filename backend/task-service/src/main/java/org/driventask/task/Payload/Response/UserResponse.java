package org.driventask.task.Payload.Response;

public record UserResponse(
    String id,
    String fullname,
    String email
) {
}