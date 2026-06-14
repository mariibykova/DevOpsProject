package com.devops.project.devops_project.exception;

import com.devops.project.devops_project.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundShouldReturn404() {
        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("missing"),
                request("/api/x")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(body);
        assertEquals("missing", body.message());
    }

    @Test
    void handleValidationShouldReturnFieldMap() throws NoSuchMethodException {
        Method method = Dummy.class.getDeclaredMethod("register", RegisterRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "invalid email"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ApiErrorResponse> response = handler.handleValidation(ex, request("/api/auth/register"));
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Validation failed", body.message());
        assertEquals("invalid email", body.validationErrors().get("email"));
    }

    @Test
    void handleConstraintViolationShouldReturn400() {
        ResponseEntity<ApiErrorResponse> response = handler.handleConstraintViolation(
                new ConstraintViolationException("constraint", Set.of()),
                request("/api/x")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("constraint", body.message());
    }

    @Test
    void handleNotReadableShouldReturn400() {
        ResponseEntity<ApiErrorResponse> response = handler.handleNotReadable(
                new HttpMessageNotReadableException("bad", new MockHttpInputMessage(new byte[0])),
                request("/api/x")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Malformed JSON request", body.message());
    }

    @Test
    void handleResponseStatusShouldUseExplicitReason() {
        ResponseEntity<ApiErrorResponse> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "custom reason"),
                request("/api/x")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("custom reason", body.message());
    }

    @Test
    void handleResponseStatusShouldUseDefaultReasonPhraseWhenReasonMissing() {
        ResponseEntity<ApiErrorResponse> response = handler.handleResponseStatus(
                new ResponseStatusException(HttpStatus.NOT_FOUND),
                request("/api/x")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Not Found", body.message());
    }

    @Test
    void handleAuthenticationShouldReturn401() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAuthentication(
                new BadCredentialsException("bad creds"),
                request("/api/auth/login")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Invalid email or password", body.message());
    }

    @Test
    void handleUnexpectedShouldReturn500() {
        ResponseEntity<ApiErrorResponse> response = handler.handleUnexpected(
                new RuntimeException("x"),
                request("/api/x")
        );
        ApiErrorResponse body = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Unexpected server error", body.message());
        assertNotNull(body.timestamp());
        assertNull(body.validationErrors());
    }

    private HttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    static class Dummy {
        void register(RegisterRequest request) {
        }
    }
}
