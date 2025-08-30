package com.app.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Getter
public class ErrorResponse {

    private static final String MESSAGE_FORMAT = "[%s] %s";
    private static final String MESSAGE_DELIMITER = ", ";

    private final String code;
    private final String message;

    @Builder
    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResponse of(int code, String message) {
        return ErrorResponse.builder()
                .code(String.valueOf(code))
                .message(message)
                .build();
    }

    public static ErrorResponse of(int code, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .code(String.valueOf(code))
                .message(createMessage(bindingResult))
                .build();
    }

    private static String createMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(fieldError -> String.format(MESSAGE_FORMAT, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(MESSAGE_DELIMITER));
    }
}
