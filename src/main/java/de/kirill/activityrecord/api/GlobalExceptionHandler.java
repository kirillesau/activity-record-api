package de.kirill.activityrecord.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * GlobalExceptionHandler is a centralized exception handling component
 * that handles exceptions thrown by the application and returns appropriate
 * HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException and returns a 400 Bad Request response.
     *
     * @param exception the IllegalArgumentException thrown
     * @return a ResponseEntity with a 400 status and the exception message as the body
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFoundException(IllegalArgumentException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
    }
}
