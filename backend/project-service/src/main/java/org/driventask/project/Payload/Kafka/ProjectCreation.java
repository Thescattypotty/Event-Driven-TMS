package org.driventask.project.Payload.Kafka;

import java.time.LocalDateTime;

public record ProjectCreation (
    String id,
    String userId,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime startDate,
    LocalDateTime endDate
){
}
