package com.globus.userservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    private ErrorResponse errorResponse;

    @BeforeEach
    void setUp() {
        errorResponse = new ErrorResponse("2024-04-30T12:00",
                404,
                "Not found error",
                "/api/v1/test");
    }

    @Test
    void getTimestamp() {
        assertEquals("2024-04-30T12:00", errorResponse.getTimestamp());
    }

    @Test
    void getStatus() {
        assertEquals(404, errorResponse.getStatus());
    }

    @Test
    void getError() {
        assertEquals("Not found error", errorResponse.getError());
    }

    @Test
    void getPath() {
        assertEquals("/api/v1/test", errorResponse.getPath());
    }

    @Test
    void setTimestamp() {
        errorResponse.setTimestamp("2024-01-01T00:00");
        assertEquals("2024-01-01T00:00", errorResponse.getTimestamp());
    }

    @Test
    void setStatus() {
        errorResponse.setStatus(400);
        assertEquals(400, errorResponse.getStatus());
    }

    @Test
    void setError() {
        errorResponse.setError("Bad request");
        assertEquals("Bad request", errorResponse.getError());
    }

    @Test
    void setPath() {
        errorResponse.setPath("/error/path");
        assertEquals("/error/path", errorResponse.getPath());
    }

    @Test
    void testEquals() {
        ErrorResponse other = new ErrorResponse("2024-04-30T12:00", 404, "Not found error", "/api/v1/test");
        assertEquals(errorResponse, other);
        assertNotEquals(errorResponse, null);
        assertNotEquals(errorResponse, "string");
    }

    @Test
    void canEqual() {

        ErrorResponse other = new ErrorResponse(null, 0, null, null);
        assertTrue(errorResponse.canEqual(other));
        assertFalse(errorResponse.canEqual("string"));
    }

    @Test
    void testHashCode() {
        ErrorResponse other = new ErrorResponse("2024-04-30T12:00", 404, "Not found error", "/api/v1/test");
        assertEquals(errorResponse.hashCode(), other.hashCode());
    }

    @Test
    void testToString() {
        String str = errorResponse.toString();
        assertTrue(str.contains("2024-04-30T12:00"));
        assertTrue(str.contains("404"));
        assertTrue(str.contains("Not found error"));
        assertTrue(str.contains("/api/v1/test"));
    }


    @Test
    void ctorWithStubbedWebRequest() {

        class HttpStatusStub {
            private final int code;
            HttpStatusStub(int code) { this.code = code; }
            public int value() { return code; }
        }

        class WebRequestStub {}
        class ServletWebRequestStub extends WebRequestStub {
            String getRequestURI() { return "/stub/path"; }
        }


        class ErrorResponseStub extends ErrorResponse {
            public ErrorResponseStub(String message, HttpStatusStub status, WebRequestStub request) {
                // жестко подаем поля
                super("timestamp", status.value(), message, request instanceof ServletWebRequestStub ? ((ServletWebRequestStub)request).getRequestURI() : null);
            }
        }


        HttpStatusStub statusStub = new HttpStatusStub(418);
        ServletWebRequestStub reqStub = new ServletWebRequestStub();
        ErrorResponse fromCtor = new ErrorResponseStub("stub-message", statusStub, reqStub);

        assertEquals("stub-message", fromCtor.getError());
        assertEquals(418, fromCtor.getStatus());
        assertEquals("/stub/path", fromCtor.getPath());
        assertEquals("timestamp", fromCtor.getTimestamp());
    }
}