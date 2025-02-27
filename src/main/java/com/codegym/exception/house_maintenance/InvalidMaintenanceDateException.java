package com.codegym.exception.house_maintenance;

public class InvalidMaintenanceDateException extends RuntimeException {
    public InvalidMaintenanceDateException(String message) {
        super(message);
    }
}
