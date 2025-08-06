package com.app.global.jwt.service;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.AuthenticationScheme;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    private static final String SPLIT_REGEX = "\\s+";
    private static final int EXPECTED_ARRAY_LENGTH = 2;
    private static final int AUTHENTICATION_SCHEME_INDEX = 0;
    private static final int TOKEN_INDEX = 1;

    public String extractToken(String authorizationHeader) {
        String[] authorizationHeaderElements = authorizationHeader.strip().split(SPLIT_REGEX);
        validateAuthorizationHeaderFormat(authorizationHeaderElements);
        return authorizationHeaderElements[TOKEN_INDEX];
    }

    private void validateAuthorizationHeaderFormat(String[] authorizationHeaderElements) {
        if (isInvalidAuthorizationHeaderFormat(authorizationHeaderElements)) {
            throw new AuthenticationException(ErrorType.INVALID_AUTHORIZATION_HEADER_FORMAT);
        }
    }

    private boolean isInvalidAuthorizationHeaderFormat(String[] authorizationHeaderElements) {
        return authorizationHeaderElements.length != EXPECTED_ARRAY_LENGTH
                || !authorizationHeaderElements[AUTHENTICATION_SCHEME_INDEX].equals(AuthenticationScheme.BEARER.getText());
    }
}
