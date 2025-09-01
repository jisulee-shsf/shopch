package com.shopch.global.jwt;

import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthenticationException;
import com.shopch.global.jwt.constant.AuthenticationScheme;
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
        validateAuthorizationHeader(authorizationHeader);

        return authorizationHeader.substring(authenticationScheme.getPrefix().length()).trim();
    }

    private void validateAuthorizationHeader(String authorizationHeader) {
        if (hasNoAuthorizationHeader(authorizationHeader)) {
            throw new AuthenticationException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }

        if (hasInvalidAuthenticationScheme(authorizationHeader)) {
            throw new AuthenticationException(ErrorCode.INVALID_AUTHORIZATION_HEADER);
        }
    }

    private boolean hasNoAuthorizationHeader(String authorizationHeader) {
        return !StringUtils.hasText(authorizationHeader);
    }

    private boolean hasInvalidAuthenticationScheme(String authorizationHeader) {
        return !StringUtils.startsWithIgnoreCase(authorizationHeader, authenticationScheme.getPrefix());
    }
}
