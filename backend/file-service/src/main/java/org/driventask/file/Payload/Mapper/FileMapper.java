package org.driventask.file.Payload.Mapper;

import org.driventask.file.Entity.FileMetadata;
import org.driventask.file.Payload.Response.FileResponse;
import org.springframework.stereotype.Service;

@Service
public class FileMapper {
    public FileResponse fromFile(FileMetadata fileMetadata, byte[] file){
        return new FileResponse(
            fileMetadata.getId(),
            fileMetadata.getFileName(),
            fileMetadata.getContentType(),
            fileMetadata.getSize(),
            file,
            fileMetadata.getUploadedDate()
        );
    }
}
