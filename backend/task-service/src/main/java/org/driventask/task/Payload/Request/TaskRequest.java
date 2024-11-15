package org.driventask.task.Payload.Request;

import java.util.Set;

import org.driventask.task.Enum.EPriority;
import org.driventask.task.Enum.EStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequest(
    
    @NotBlank
    @NotNull
    String title,
    
    String description,
    
    EPriority priority,

    EStatus status,

    String userAssigned,

    @NotNull
    @NotBlank
    String projectId,

    Set<String> filesIncluded
) {
    
}
