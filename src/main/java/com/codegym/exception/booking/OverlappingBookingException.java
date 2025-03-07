package com.codegym.exception.booking;

public class OverlappingBookingException extends RuntimeException {
    public OverlappingBookingException(String message) {
        super(message);
    }
}
