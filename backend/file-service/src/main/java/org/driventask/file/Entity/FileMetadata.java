package org.driventask.file.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "fileMetadata")
public class FileMetadata {
    
    @Id
    private String id;
    private String fileName;
    private String contentType;
    private long size;

    private String uploadedDate;
}
