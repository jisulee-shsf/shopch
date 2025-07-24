package com.app.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Getter
public class ErrorResponse {

    private static final String ERROR_MESSAGE_FORMAT = "[%s] %s";
    private static final String ERROR_MESSAGE_DELIMITER = ", ";

    private final String errorCode;
    private final String errorMessage;

    @Builder
    private ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(String errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public static ErrorResponse of(int errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(String.valueOf(errorCode))
                .errorMessage(errorMessage)
                .build();
    }

    public static ErrorResponse of(int errorCode, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .errorCode(String.valueOf(errorCode))
                .errorMessage(createErrorMessage(bindingResult))
                .build();
    }

    private static String createErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(fieldError -> String.format(ERROR_MESSAGE_FORMAT, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(ERROR_MESSAGE_DELIMITER));
    }
}
