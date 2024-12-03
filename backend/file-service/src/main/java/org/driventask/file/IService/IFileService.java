package org.driventask.file.IService;

import org.driventask.file.Payload.Request.FileRequest;
import org.driventask.file.Payload.Response.FileResponse;

public interface IFileService {
    String storeFile(FileRequest fileRequest);
    FileResponse getFile(String fileId);
    void deleteFile(String fileId);
    boolean isFileExists(String fileId);
}
