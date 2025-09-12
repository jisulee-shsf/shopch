package com.shopch.global.interceptor;

import com.shopch.global.auth.BearerTokenExtractor;
import com.shopch.global.auth.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final BearerTokenExtractor bearerTokenExtractor;
    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = bearerTokenExtractor.extractToken(request);
        jwtProvider.validateAccessToken(accessToken);
        return true;
    }
}
