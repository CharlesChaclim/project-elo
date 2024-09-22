package br.com.project.exception;

import br.com.project.errors.StanderError;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StanderError> objectNotFound(ObjectNotFoundException ex, HttpServletRequest httpServletRequest) {
        StanderError error = new StanderError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage(), httpServletRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StanderError> objectNotFound(BadRequestException ex, HttpServletRequest httpServletRequest) {
        StanderError error = new StanderError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), httpServletRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StanderError> objectNotFound(IllegalArgumentException ex, HttpServletRequest httpServletRequest) {
        StanderError error = new StanderError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), httpServletRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StanderError> exception(Exception ex, HttpServletRequest httpServletRequest) {
        StanderError error = new StanderError(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), httpServletRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
