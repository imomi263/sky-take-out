package com.sky.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {}
    public LoginFailedException(String message) {
        super(message);
    }
}
