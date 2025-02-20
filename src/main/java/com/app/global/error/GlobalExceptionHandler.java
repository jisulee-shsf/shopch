package com.app.global.error;

import com.app.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("BindException", e);
        ErrorResponse response = ErrorResponse.of(String.valueOf(BAD_REQUEST.value()), e.getBindingResult());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        ErrorResponse response = ErrorResponse.of(String.valueOf(BAD_REQUEST.value()), e.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        ErrorResponse response = ErrorResponse.of(String.valueOf(METHOD_NOT_ALLOWED.value()), e.getMessage());
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("NoResourceFoundException", e);
        ErrorResponse response = ErrorResponse.of(String.valueOf(NOT_FOUND.value()), e.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException", e);
        ErrorResponse response = ErrorResponse.of(e.getErrorType().getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorType().getHttpStatus()).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception", e);
        ErrorResponse response = ErrorResponse.of(String.valueOf(INTERNAL_SERVER_ERROR.value()), e.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
}
