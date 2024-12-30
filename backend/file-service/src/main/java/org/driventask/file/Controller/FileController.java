package org.driventask.file.Controller;

import org.driventask.file.Payload.Request.FileRequest;
import org.driventask.file.Payload.Response.FileResponse;
import org.driventask.file.Service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestBody @Valid FileRequest fileRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.storeFile(fileRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> downloadFile(@PathVariable("id") String fileId){
        return ResponseEntity.ok(fileService.getFile(fileId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable("id") String fileId){
        fileService.deleteFile(fileId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("File Deleted Successfully");
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<Boolean> isFileExisting(@PathVariable("id") String fileId){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(fileService.isFileExists(fileId));
    }
}
