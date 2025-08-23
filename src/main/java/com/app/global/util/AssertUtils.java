package com.app.global.util;

import com.app.global.error.ErrorCode;
import com.app.global.error.exception.IllegalArgumentException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertUtils {

    public static void notNull(Object object, ErrorCode errorCode) {
        if (object == null) {
            throw new IllegalArgumentException(errorCode);
        }
    }

    public static void hasText(String text, ErrorCode errorCode) {
        if (hasNoText(text)) {
            throw new IllegalArgumentException(errorCode);
        }
    }

    private static boolean hasNoText(String text) {
        return !StringUtils.hasText(text);
    }
}
