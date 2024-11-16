package org.driventask.project.Payload.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequest() {
    @NotNull
    @NotBlank
    String name,

    String description,

}
