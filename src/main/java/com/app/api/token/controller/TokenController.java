package com.app.api.token.controller;

import com.app.api.token.service.TokenService;
import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.global.jwt.service.TokenExtractor;
import com.app.global.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final TokenExtractor tokenExtractor;

    @PostMapping("/api/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(HttpServletRequest request) {
        String authorizationHeader = AuthorizationHeaderUtils.getAuthorizationHeader(request);
        String refreshToken = tokenExtractor.extractToken(authorizationHeader);
        Date issueDate = new Date();
        return ResponseEntity.ok(tokenService.refreshAccessToken(refreshToken, issueDate));
    }
}
