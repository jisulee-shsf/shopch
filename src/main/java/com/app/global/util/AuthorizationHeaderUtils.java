package com.app.global.util;

import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.GrantType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import static com.app.global.error.ErrorType.INVALID_GRANT_TYPE;
import static com.app.global.error.ErrorType.MISSING_AUTHORIZATION_HEADER;

public class AuthorizationHeaderUtils {

    public static String getAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);
        return authorizationHeader;
    }

    public static String extractToken(String authorizationHeader) {
        String[] authorizationHeaderElements = splitAuthorizationHeader(authorizationHeader);
        validateAuthorizationHeaderFormat(authorizationHeaderElements);
        return authorizationHeaderElements[1];
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
        return authorizationHeader.split(" ");
    }

    private static void validateAuthorizationHeaderFormat(String[] authorizationHeaderElements) {
        if (isInvalidAuthorizationHeaderFormat(authorizationHeaderElements)) {
            throw new AuthenticationException(INVALID_GRANT_TYPE);
        }
    }

    private static boolean isInvalidAuthorizationHeaderFormat(String[] authorizationHeaderElements) {
        return authorizationHeaderElements.length < 2
                || !authorizationHeaderElements[0].equals(GrantType.BEARER.getType());
    }
}
