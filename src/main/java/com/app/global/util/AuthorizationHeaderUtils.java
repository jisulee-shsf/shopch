package com.app.global.util;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class AuthorizationHeaderUtils {

    public static String getAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);
        return authorizationHeader;
    }

    private static void validateAuthorizationHeader(String authorizationHeader) {
        if (ValidationUtils.hasNoText(authorizationHeader)) {
            throw new AuthenticationException(ErrorType.MISSING_AUTHORIZATION_HEADER);
        }
    }
}
