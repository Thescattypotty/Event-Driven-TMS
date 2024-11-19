package org.driventask.user.Payload.Kafka;

import java.time.LocalDateTime;

public record UserCreation(
    String id,
    String fullName,
    String email,
    LocalDateTime creationDate
) {
    
}
