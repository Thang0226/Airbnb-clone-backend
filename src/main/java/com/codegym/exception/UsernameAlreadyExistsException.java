package com.codegym.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    private String message;

    public UsernameAlreadyExistsException() {}

    public UsernameAlreadyExistsException(String msg) {
        super(msg);
        this.message = msg;
    }
}
