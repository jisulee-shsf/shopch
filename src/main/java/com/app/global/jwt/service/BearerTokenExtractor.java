package com.app.global.jwt.service;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.AuthenticationScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BearerTokenExtractor {

    private final AuthenticationScheme authenticationScheme;

    public BearerTokenExtractor() {
        this.authenticationScheme = AuthenticationScheme.BEARER;
    }

    public String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (hasNoAuthorizationHeader(authorizationHeader)) {
            throw new AuthenticationException(ErrorType.MISSING_AUTHORIZATION_HEADER);
        }

        if (hasInvalidAuthenticationScheme(authorizationHeader)) {
            throw new AuthenticationException(ErrorType.INVALID_AUTHORIZATION_HEADER);
        }

        return authorizationHeader.substring(authenticationScheme.getPrefix().length()).trim();
    }

    private boolean hasNoAuthorizationHeader(String authorizationHeader) {
        return !StringUtils.hasText(authorizationHeader);
    }

    private boolean hasInvalidAuthenticationScheme(String authorizationHeader) {
        return !StringUtils.startsWithIgnoreCase(authorizationHeader, authenticationScheme.getPrefix());
    }
}
