package org.driventask.project.Exception;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException(String message){
        super(message);
    }
}
