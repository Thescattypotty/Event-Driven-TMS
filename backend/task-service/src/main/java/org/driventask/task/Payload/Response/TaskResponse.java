package org.driventask.task.Payload.Response;

import java.time.LocalDateTime;
import java.util.Set;

import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;

public record TaskResponse(
    String id,

    String title,

    String description,

    LocalDateTime dueDate,

    EPriority priority,

    EStatus status,
    
    Set<String> historyOfTask,

    Set<String> filesIncluded,

    String userAssigned,

    String projectId,

    LocalDateTime createdAt,

    LocalDateTime updatedAt

) {
    
}
