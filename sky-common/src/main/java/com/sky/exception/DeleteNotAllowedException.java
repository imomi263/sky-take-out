package com.sky.exception;

public class DeleteNotAllowedException extends BaseException{
    public DeleteNotAllowedException() {

    }
    public DeleteNotAllowedException(String message) {

        super(message);
    }
}
