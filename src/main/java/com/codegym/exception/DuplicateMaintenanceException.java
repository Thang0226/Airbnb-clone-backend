package com.codegym.exception;

public class DuplicateMaintenanceException extends RuntimeException {
    public DuplicateMaintenanceException(String message) {
        super(message);
    }
}
