package com.app.global.util;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.IllegalArgumentException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

    public static void notNull(Object object, ErrorType errorType) {
        if (object == null) {
            throw new IllegalArgumentException(errorType);
        }
    }

    public static void hasText(String text, ErrorType errorType) {
        if (hasNoText(text)) {
            throw new IllegalArgumentException(errorType);
        }
    }

    public static boolean hasNoText(String text) {
        return !StringUtils.hasText(text);
    }
}
