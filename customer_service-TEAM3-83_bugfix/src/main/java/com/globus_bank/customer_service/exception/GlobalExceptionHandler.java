package com.globus_bank.customer_service.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.globus_bank.customer_service.dto.response.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .uuid(UUID.randomUUID().toString())
                .error("Validation Errors")
                .message(String.join(", ", errors))
                .time(Instant.now().toString())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();
        if(rootCause instanceof InvalidFormatException invalidFormatEx) {
            String fullPath = invalidFormatEx.getPathReference();
            int lastIndexOfDot = fullPath.lastIndexOf('.');
            String fieldName = fullPath.substring(lastIndexOfDot + 1);
            
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .uuid(UUID.randomUUID().toString())
                    .error("Invalid format for field")
                    .message("Неверный формат поля " + fieldName)
                    .time(Instant.now().toString())
                    .build();
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .uuid(UUID.randomUUID().toString())
                    .error("Empty or malformed request body")
                    .message("Запрос содержит ошибки формата или пуст")
                    .time(Instant.now().toString())
                    .build();
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
    
    @ExceptionHandler(ClientWithTinAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleClientWithTinAlreadyExists(ClientWithTinAlreadyExistsException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .error("ClientWithTinAlreadyExists")
                .uuid(UUID.randomUUID().toString())
                .time(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(InvalidClientTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidClientType(InvalidClientTypeException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .error("InvalidClientType")
                .uuid(UUID.randomUUID().toString())
                .time(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(BankDoesNotWorkWithIndividualsException.class)
    public ResponseEntity<ErrorResponse> handleBankDoesNotWorkWithIndividuals(BankDoesNotWorkWithIndividualsException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .error("BankDoesNotWorkWithIndividuals")
                .uuid(UUID.randomUUID().toString())
                .time(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .uuid(UUID.randomUUID().toString())
                .error("Not Found")
                .message(ex.getMessage())
                .time(Instant.now().toString())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .uuid(UUID.randomUUID().toString())
                .error("Internal Server Error")
                .message("Внутренняя ошибка на сервере")
                .time(Instant.now().toString())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(DocumentCustomerMismatchException.class)
    public ResponseEntity<ErrorResponse> handleDocumentCustomerMismatchException(DocumentCustomerMismatchException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .uuid(UUID.randomUUID().toString()).error("DocumentCustomerMismatch")
                .message(ex.getMessage()).time(Instant.now().toString())
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
