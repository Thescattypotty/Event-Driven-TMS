package org.driventask.project.ExceptionHanlder;

import org.driventask.project.Exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProjectNotFoundException.class)
    public Mono<ResponseEntity<String>> handleException(ProjectNotFoundException projectNotFoundException){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(projectNotFoundException.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + ex.getMessage()));
    }

}
