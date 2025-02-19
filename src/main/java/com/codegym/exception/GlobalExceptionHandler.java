package com.codegym.exception;



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
}
