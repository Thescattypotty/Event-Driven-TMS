package org.driventask.user.Payload.Kafka;

import java.time.LocalDateTime;

import org.driventask.user.Enum.EUserEvent;

public record UserUpdated(
    String id,
    String fullName,
    String email,
    EUserEvent event,
    LocalDateTime updateDate
) {
    
}
