package com.app.global.interceptor;

import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.TokenType;
import com.app.global.jwt.service.TokenManager;
import com.app.global.util.AuthorizationHeaderUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.app.global.error.ErrorType.INVALID_TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = AuthorizationHeaderUtils.getAuthorizationHeader(request);
        String accessToken = AuthorizationHeaderUtils.extractToken(authorizationHeader);
        validateAccessToken(accessToken);
        return true;
    }

    private void validateAccessToken(String accessToken) {
        tokenManager.validateToken(accessToken);

        Claims claims = tokenManager.getTokenClaims(accessToken);
        String tokenType = claims.getSubject();
        if (TokenType.isAccessToken(tokenType)) {
            return;
        }
        throw new AuthenticationException(INVALID_TOKEN_TYPE);
    }
}
