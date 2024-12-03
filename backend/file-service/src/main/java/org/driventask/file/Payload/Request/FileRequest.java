package org.driventask.file.Payload.Request;

public record FileRequest(
    String fileName,
    String contentType,
    long size,
    byte[] file
) {
    
}
