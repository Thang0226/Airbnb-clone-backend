package com.codegym.exception.house_maintenance;

public class DuplicateMaintenanceException extends RuntimeException {
    public DuplicateMaintenanceException(String message) {
        super(message);
    }
}
