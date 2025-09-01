package com.shopch.global.interceptor;

import com.shopch.global.jwt.BearerTokenExtractor;
import com.shopch.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final BearerTokenExtractor bearerTokenExtractor;
    private final JwtTokenProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = bearerTokenExtractor.extractToken(request);
        jwtProvider.validateAccessToken(accessToken);
        return true;
    }
}
