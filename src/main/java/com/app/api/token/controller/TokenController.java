package com.app.api.token.controller;

import com.app.api.token.controller.dto.request.RefreshAccessTokenRequest;
import com.app.api.token.service.TokenService;
import com.app.api.token.service.dto.response.AccessTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/api/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@Valid @RequestBody RefreshAccessTokenRequest request) {
        Date issuedAt = new Date();
        return ResponseEntity.ok(tokenService.refreshAccessToken(request.toServiceRequest(), issuedAt));
    }
}
