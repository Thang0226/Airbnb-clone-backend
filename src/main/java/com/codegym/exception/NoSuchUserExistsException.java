package com.codegym.exception;

public class NoSuchUserExistsException extends RuntimeException {
    private String message;

    public NoSuchUserExistsException() {}

    public NoSuchUserExistsException(String msg) {
        super(msg);
        this.message = msg;
    }
}
