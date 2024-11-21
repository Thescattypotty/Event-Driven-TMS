package org.driventask.project.Payload.Kafka;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectUpdate (
    UUID id,
    UUID userId,
    String name,
    String description,
    LocalDateTime updatedAt,
    LocalDateTime startDate,
    LocalDateTime endDate

) {

    
    
}
