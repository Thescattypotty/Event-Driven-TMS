package org.driventask.file.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bson.types.ObjectId;
import org.driventask.file.Entity.FileMetadata;
import org.driventask.file.Exception.FileException;
import org.driventask.file.IService.IFileService;
import org.driventask.file.Payload.Mapper.FileMapper;
import org.driventask.file.Payload.Request.FileRequest;
import org.driventask.file.Payload.Response.FileResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService{
    
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;
    private final FileMapper fileMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public String storeFile(FileRequest fileRequest) {
        ObjectId fileId = gridFsTemplate.store(
            new ByteArrayInputStream(fileRequest.file()),
            fileRequest.fileName(),
            fileRequest.contentType()
        );      
        FileMetadata metadata = new FileMetadata(
            fileId.toString(),
            fileRequest.fileName(),
            fileRequest.contentType(),
            fileRequest.size(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );    
        mongoTemplate.save(metadata);
        return fileId.toString();   
    }

    @Override
    public FileResponse getFile(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if(gridFSFile != null)
        {
            try {
                InputStream inputStream = gridFsOperations.getResource(gridFSFile).getInputStream();
                return fileMapper.fromFile(mongoTemplate.findById(fileId, FileMetadata.class),StreamUtils.copyToByteArray(inputStream));
            } catch (IOException e) {
                throw new FileException("Failed to render file as Byte[]");
            }
        }else{
            throw new FileException("File Metadata not found");
        }
    }
    
    @Override
    @Transactional
    public void deleteFile(String fileId) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
        long deletedCount = mongoTemplate.remove(new Query(Criteria.where("_id").is(fileId)), FileMetadata.class)
                .getDeletedCount();
        if (deletedCount == 0) {
            throw new FileException("File Metadata not found or already deleted");
        }
    }

    @Override
    public boolean isFileExists(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if(gridFSFile != null){
            return true;
        }
        return false;
    }


}
