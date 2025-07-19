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
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public static void validateAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new AuthenticationException(MISSING_AUTHORIZATION_HEADER);
        }

        String[] authorizations = authorizationHeader.split(" ");
        if (authorizations.length < 2 || !authorizations[0].equals(GrantType.BEARER.getType())) {
            throw new AuthenticationException(INVALID_GRANT_TYPE);
        }
    }
}
