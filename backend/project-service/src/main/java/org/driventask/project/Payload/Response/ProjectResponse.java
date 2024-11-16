package org.driventask.project.Payload.Response;

import java.time.LocalDateTime;

public record ProjectResponse() {
    String name;

    String description,

    LocalDateTime startDate,

    LocalDateTime endDate,


}

