package org.driventask.project.Payload.Request;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequest(
    @NotNull
    @NotBlank
    String name,

    String description,
    
    LocalDateTime startDate,
    
    LocalDateTime endDate,
    
    Set<String> userId,
    
    Set<String> file_id
) {

}
