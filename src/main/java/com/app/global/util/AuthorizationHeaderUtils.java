package com.app.global.util;

import com.app.global.error.exception.AuthenticationException;
import org.springframework.util.StringUtils;

import static com.app.global.error.ErrorType.INVALID_GRANT_TYPE;
import static com.app.global.error.ErrorType.MISSING_AUTHORIZATION_HEADER;
import static com.app.global.jwt.constant.GrantType.BEARER;

public class AuthorizationHeaderUtils {

    public static void validateAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new AuthenticationException(MISSING_AUTHORIZATION_HEADER);
        }

        String[] authorizations = authorizationHeader.split(" ");
        if (authorizations.length < 2 || !authorizations[0].equals(BEARER.getType())) {
            throw new AuthenticationException(INVALID_GRANT_TYPE);
        }
    }
}
