package org.driventask.project.Payload.Request;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequest(
    @NotNull
    @NotBlank
    String name ,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String userId,
    Set<String> file_id
) {

}
