package com.app.api.token.controller;

import com.app.api.token.dto.AccessTokenResponse;
import com.app.api.token.service.AccessTokenService;
import com.app.global.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    @PostMapping("/api/access-token/issue")
    public ResponseEntity<AccessTokenResponse> createAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        AuthorizationHeaderUtils.validateAuthorizationHeader(authorizationHeader);

        String refreshToken = authorizationHeader.split(" ")[1];
        return ResponseEntity.ok(accessTokenService.createAccessTokenByRefreshToken(refreshToken, new Date()));
    }
}
