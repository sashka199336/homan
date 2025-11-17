import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    
    static class ErrorResponse {
        final String message;
        final HttpStatus status;
        final Object request;

        ErrorResponse(String message, HttpStatus status, Object request) {
            this.message = message;
            this.status = status;
            this.request = request;
        }
    }
    static class EntityNotFoundException extends Exception {
        EntityNotFoundException(String msg) { super(msg); }
    }
    static class IllegalUserStateException extends RuntimeException {
        IllegalUserStateException(String msg) { super(msg); }
    }
    static class ValidationException extends RuntimeException {
        ValidationException(String msg) { super(msg); }
    }
    interface WebRequest {}


    static class GlobalExceptionHandler {
        public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e, WebRequest request) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        public ResponseEntity<ErrorResponse> handleIllegalDataForTransferException(RuntimeException e, WebRequest request) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.NOT_ACCEPTABLE, request);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
        }
        public ResponseEntity<ErrorResponse> handleValidationException(RuntimeException e, WebRequest request) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    GlobalExceptionHandler handler;
    WebRequest stubRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        stubRequest = new WebRequest() {};
    }

    @Test
    void handleNotFoundException() throws Exception {
        Exception ex = new EntityNotFoundException("нет сущности");
        ResponseEntity<ErrorResponse> resp = handler.handleNotFoundException(ex, stubRequest);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("нет сущности", resp.getBody().message);
        assertEquals(stubRequest, resp.getBody().request);
    }

    @Test
    void handleIllegalDataForTransferException() {
        RuntimeException ex = new IllegalUserStateException("некорректно");
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalDataForTransferException(ex, stubRequest);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, resp.getStatusCode());
        assertEquals("некорректно", resp.getBody().message);
        assertEquals(stubRequest, resp.getBody().request);
    }

    @Test
    void handleValidationException() {
        RuntimeException ex = new ValidationException("Ошибка валидации");
        ResponseEntity<ErrorResponse> resp = handler.handleValidationException(ex, stubRequest);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Ошибка валидации", resp.getBody().message);
        assertEquals(stubRequest, resp.getBody().request);
    }
}