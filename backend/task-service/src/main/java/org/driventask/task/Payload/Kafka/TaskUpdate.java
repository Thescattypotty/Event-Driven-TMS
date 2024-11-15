package org.driventask.task.Payload.Kafka;

import java.time.LocalDateTime;

import org.driventask.task.Enum.EStatus;

public record TaskUpdate(
    String id,
    String title,
    EStatus status,
    String userAssigned,
    String projectId,
    LocalDateTime creationDate,
    LocalDateTime updatedDate
) {
    
}
