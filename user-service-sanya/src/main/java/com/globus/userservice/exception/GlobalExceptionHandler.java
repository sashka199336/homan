package com.globus.userservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<AppError> catchUserNotFoundException(UserNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchEntityNotFoundException(EntityNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchIllegalUserStateException(IllegalUserStateException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_ACCEPTABLE.value(),
                exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }
}
