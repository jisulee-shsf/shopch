package com.app.global.util;

import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.AuthenticationScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import static com.app.global.error.ErrorType.INVALID_GRANT_TYPE;
import static com.app.global.error.ErrorType.MISSING_AUTHORIZATION_HEADER;

public class AuthorizationHeaderUtils {

    private static final String DELIMITER = " ";
    private static final int MIN_ARRAY_LENGTH = 2;
    private static final int AUTHENTICATION_SCHEME_INDEX = 0;
    private static final int TOKEN_INDEX = 1;

    public static String getAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);
        return authorizationHeader;
    }

    public static String extractToken(String authorizationHeader) {
        String[] authorizationHeaderElements = splitAuthorizationHeader(authorizationHeader);
        validateAuthorizationHeaderFormat(authorizationHeaderElements);
        return authorizationHeaderElements[TOKEN_INDEX];
    }

    private static void validateAuthorizationHeader(String authorizationHeader) {
        if (hasNoAuthorizationHeader(authorizationHeader)) {
            throw new AuthenticationException(MISSING_AUTHORIZATION_HEADER);
        }
    }

    private static boolean hasNoAuthorizationHeader(String authorizationHeader) {
        return !StringUtils.hasText(authorizationHeader);
    }

    private static String[] splitAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader.split(DELIMITER);
    }

    private static void validateAuthorizationHeaderFormat(String[] authorizationHeaderElements) {
        if (isInvalidAuthorizationHeaderFormat(authorizationHeaderElements)) {
            throw new AuthenticationException(INVALID_GRANT_TYPE);
        }
    }

    private static boolean isInvalidAuthorizationHeaderFormat(String[] authorizationHeaderElements) {
        return authorizationHeaderElements.length < MIN_ARRAY_LENGTH
                || !authorizationHeaderElements[AUTHENTICATION_SCHEME_INDEX].equals(AuthenticationScheme.BEARER.getText());
    }
}
