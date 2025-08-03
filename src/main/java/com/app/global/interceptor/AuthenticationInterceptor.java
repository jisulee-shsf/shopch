package com.app.global.interceptor;

import com.app.global.jwt.service.TokenExtractor;
import com.app.global.jwt.service.TokenManager;
import com.app.global.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenExtractor tokenExtractor;
    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = AuthorizationHeaderUtils.getAuthorizationHeader(request);
        String accessToken = tokenExtractor.extractToken(authorizationHeader);
        tokenManager.validateAccessToken(accessToken);
        return true;
    }
}
