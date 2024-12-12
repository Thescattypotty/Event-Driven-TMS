package org.driventask.project.Payload.Kafka;

import java.time.LocalDateTime;

public record ProjectUpdate (
    String id,
    String userId,
    String name,
    String description,
    LocalDateTime updatedAt,
    LocalDateTime startDate,
    LocalDateTime endDate

) {

}
