package org.driventask.task.Exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message){
        super(message);
    }
}