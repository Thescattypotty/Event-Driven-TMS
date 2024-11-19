package org.driventask.auth.Exception;

public class BadCredentialsException extends RuntimeException{
    public BadCredentialsException(String message){
        super(message);
    }

    public BadCredentialsException(String message, Throwable e) {
        super(message, e);
    }
}
