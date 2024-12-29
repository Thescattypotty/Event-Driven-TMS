package org.driventask.task.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.driventask.task.Exception.ProjectNotFoundException;
import org.driventask.task.Exception.TaskCreationException;
import org.driventask.task.Exception.TaskNotFoundException;
import org.driventask.task.Exception.TaskUpdateException;
import org.driventask.task.Exception.UserNotFoundException;
import org.driventask.task.Payload.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler({TaskNotFoundException.class , ProjectNotFoundException.class , UserNotFoundException.class})
    public Mono<ResponseEntity<ErrorResponse>>  handleException(RuntimeException runtimeException){
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND,
            runtimeException.getMessage(),
            LocalDateTime.now(),
            null
            );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler({TaskCreationException.class, TaskUpdateException.class})
    public Mono<ResponseEntity<ErrorResponse>> handleTaskExceptions(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage(),
            LocalDateTime.now(),
            null
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException){
        List<String> errorDetails = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getObjectName() + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields",
                LocalDateTime.now(),
                errorDetails
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }
}
