package com.codegym.exception;



import com.codegym.exception.booking.BookingNotFoundException;
import com.codegym.exception.booking.OverlappingBookingException;
import com.codegym.exception.house_maintenance.OverlappingMaintenanceException;
import com.codegym.exception.house_maintenance.InvalidMaintenanceDateException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;         // 5MB
    private static final long MAX_REQUEST_SIZE = 100 * 1024 * 1024;      // 100MB

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
                                                                       HttpServletRequest request) {
        long contentLength = request.getContentLengthLong();
        if (contentLength > MAX_REQUEST_SIZE) {
            return new ResponseEntity<>(
                    "Total request size exceeds the maximum allowed limit of 100MB",
                    HttpStatus.PAYLOAD_TOO_LARGE);
        } else {
            return new ResponseEntity<>(
                    "File size exceeds the maximum limit of 5MB",
                    HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    @ExceptionHandler(NoSuchUserExistsException.class)
    public ResponseEntity<?> handleNoSuchUserExistsException(NoSuchUserExistsException ex) {
        return new ResponseEntity<>(
                "User " + ex.getMessage() + " does not exist",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<String> handleAvailabilityNotFound(AvailabilityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HouseNotFoundException.class)
    public ResponseEntity<String> handleHouseNotFound(HouseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OverlappingMaintenanceException.class)
    public ResponseEntity<String> handleOverlappingMaintenanceException(OverlappingMaintenanceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidMaintenanceDateException.class)
    public ResponseEntity<?> handleInvalidMaintenanceDateException(InvalidMaintenanceDateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleBookingNotFoundException(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OverlappingBookingException.class)
    public ResponseEntity<String> handleOverlappingBookingException(OverlappingBookingException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
