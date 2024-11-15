package org.driventask.task.Payload.Kafka;

import java.time.LocalDateTime;

public record TaskCreation(
    String id,
    String title,
    String projectId,
    String userAssigned,
    LocalDateTime creationDate
) {
    
}
