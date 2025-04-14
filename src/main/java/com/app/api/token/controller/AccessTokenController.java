package com.app.api.token.controller;

import com.app.api.token.dto.AccessTokenResponse;
import com.app.api.token.service.AccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.app.global.util.AuthorizationHeaderUtils.validateAuthorizationHeader;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccessTokenController {

    private final AccessTokenService accessTokenService;

    @PostMapping("/access-token/issue")
    public ResponseEntity<AccessTokenResponse> createAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);

        String refreshToken = authorizationHeader.split(" ")[1];
        Date reissueDate = new Date();

        return ResponseEntity.ok(accessTokenService.createAccessTokenByRefreshToken(refreshToken, reissueDate));
    }
}
