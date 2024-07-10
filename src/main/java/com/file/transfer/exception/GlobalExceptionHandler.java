package com.file.transfer.exception;

import com.amazonaws.AmazonServiceException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({FileNotFoundException.class, AmazonServiceException.class})
    public ResponseEntity<ErrorResponse> handleApplicationExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), List.of(e.getMessage())));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentExceptions(Exception ex) {
        List<String> errors;

        if (ex instanceof MethodArgumentNotValidException e) {
            errors = e.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
        } else {
            errors = List.of("Invalid input: " + ((MethodArgumentTypeMismatchException) ex).getValue());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), errors));
    }
}
