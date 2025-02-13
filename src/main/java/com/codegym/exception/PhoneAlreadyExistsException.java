package com.codegym.exception;

public class PhoneAlreadyExistsException extends RuntimeException {
    private String message;

    public PhoneAlreadyExistsException() {}

    public PhoneAlreadyExistsException(String msg) {
        super(msg);
        this.message = msg;
    }
}
