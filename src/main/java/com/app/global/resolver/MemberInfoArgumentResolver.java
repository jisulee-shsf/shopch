package com.app.global.resolver;

import com.app.global.jwt.service.TokenManager;
import com.app.global.util.AuthorizationHeaderUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenManager tokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasMemberInfoAnnotation = parameter.hasParameterAnnotation(MemberInfo.class);
        boolean hasMemberInfoRequest = MemberInfoRequest.class.isAssignableFrom(parameter.getParameterType());
        return hasMemberInfoAnnotation && hasMemberInfoRequest;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorizationHeader = AuthorizationHeaderUtils.getAuthorizationHeader((HttpServletRequest) webRequest.getNativeRequest());
        String accessToken = AuthorizationHeaderUtils.extractToken(authorizationHeader);
        return extractMemberInfo(accessToken);
    }

    private MemberInfoRequest extractMemberInfo(String accessToken) {
        Claims claims = tokenManager.getTokenClaims(accessToken);
        return MemberInfoRequest.builder()
                .id(claims.get("memberId", Long.class))
                .role(claims.get("role", String.class))
                .build();
    }
}
