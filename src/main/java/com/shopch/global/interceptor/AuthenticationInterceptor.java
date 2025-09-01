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
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = bearerTokenExtractor.extractToken(request);
        jwtTokenProvider.validateAccessToken(accessToken);
        return true;
    }
}
