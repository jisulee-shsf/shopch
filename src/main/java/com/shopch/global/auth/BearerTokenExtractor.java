package com.shopch.global.auth;

import com.shopch.global.auth.constant.AuthenticationScheme;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BearerTokenExtractor {

    public String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);

        return authorizationHeader.substring(AuthenticationScheme.BEARER.getPrefix().length()).trim();
    }

    private void validateAuthorizationHeader(String authorizationHeader) {
        if (hasNoAuthorizationHeader(authorizationHeader)) {
            throw new AuthException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }

        if (hasInvalidAuthenticationScheme(authorizationHeader)) {
            throw new AuthException(ErrorCode.INVALID_AUTHORIZATION_HEADER);
        }
    }

    private boolean hasNoAuthorizationHeader(String authorizationHeader) {
        return !StringUtils.hasText(authorizationHeader);
    }

    private boolean hasInvalidAuthenticationScheme(String authorizationHeader) {
        return !StringUtils.startsWithIgnoreCase(authorizationHeader, AuthenticationScheme.BEARER.getPrefix());
    }
}
