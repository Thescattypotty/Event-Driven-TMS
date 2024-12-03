package org.driventask.file.Payload.Response;

public record FileResponse(
    String fileId,
    String fileName,
    String contentType,
    long size,
    byte[] file,
    String uploadedDate
) { 
}
