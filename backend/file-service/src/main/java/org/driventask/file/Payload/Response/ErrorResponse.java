package org.driventask.file.Payload.Response;

import java.util.Map;

public record ErrorResponse(
    Map<String, String> errors
) {
}