package org.driventask.project.Payload.Response;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    HttpStatus status,
    String message,
    LocalDateTime timestamp,
    List<String> errorDetails
) {
    
}