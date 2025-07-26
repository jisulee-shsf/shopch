package com.app.api.token.controller;

import com.app.api.token.service.AccessTokenService;
import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.global.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
        String authorizationHeader = AuthorizationHeaderUtils.getAuthorizationHeader(request);
        String refreshToken = AuthorizationHeaderUtils.extractToken(authorizationHeader);
        Date reissueDate = new Date();
        return ResponseEntity.ok(accessTokenService.createAccessTokenByRefreshToken(refreshToken, reissueDate));
    }
}
