package com.app.global.error;

import com.app.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getClass().getSimpleName(), e);
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.of(BAD_REQUEST.value(), e.getBindingResult()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(e.getClass().getSimpleName(), e);
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.of(BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getClass().getSimpleName(), e);
        return ResponseEntity.status(METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(METHOD_NOT_ALLOWED.value(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(e.getClass().getSimpleName(), e);
        return ResponseEntity.status(NOT_FOUND)
                .body(ErrorResponse.of(NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error(e.getClass().getSimpleName(), e);
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getClass().getSimpleName(), e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }
}
