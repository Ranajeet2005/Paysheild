
package com.ranajeet.paysheild.payment;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(
            PaymentNotFoundException ex) {

        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<Map<String, Object>> conflict(
            IdempotencyConflictException ex) {

        return response(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({
            InvalidRequestException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, Object>> badRequest(
            Exception ex) {

        String message;

        if (ex instanceof InvalidRequestException) {
            message = ex.getMessage();
        } else if (ex instanceof MethodArgumentNotValidException validationEx
                && validationEx.getBindingResult()
                               .getFieldError() != null) {
            message = validationEx.getBindingResult()
                    .getFieldError().getDefaultMessage();
        } else {
            message = "Invalid request. Check the request body and fields.";
        }

        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> databaseConflict(
            DataIntegrityViolationException ex) {

        return response(
            HttpStatus.CONFLICT,
            "A database constraint was violated. Check the idempotency key."
        );
    }

    private ResponseEntity<Map<String, Object>> response(
            HttpStatus status,
            String message) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
