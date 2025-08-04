package com.app.api.token.controller;

import com.app.api.token.service.TokenService;
import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.global.jwt.service.TokenExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final TokenExtractor tokenExtractor;

    @PostMapping("/api/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String refreshToken = tokenExtractor.extractToken(authorizationHeader);
        Date issueDate = new Date();
        return ResponseEntity.ok(tokenService.refreshAccessToken(refreshToken, issueDate));
    }
}
