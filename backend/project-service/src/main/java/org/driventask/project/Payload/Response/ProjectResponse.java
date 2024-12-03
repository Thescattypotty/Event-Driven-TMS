package org.driventask.project.Payload.Response;

import java.time.LocalDateTime;
import java.util.Set;

public record ProjectResponse(
    String id,

    String name,
    
    String description,
    
    LocalDateTime startDate,
    
    LocalDateTime endDate,
    
    LocalDateTime createdAt,
    
    LocalDateTime updatedAt,
    
    Set<String> users_id,
    
    Set<String> task_id,

    Set<String> file_id
) {
}

