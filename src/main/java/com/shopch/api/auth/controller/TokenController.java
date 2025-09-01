package com.shopch.api.auth.controller;

import com.shopch.api.auth.controller.dto.RefreshAccessTokenRequest;
import com.shopch.api.auth.service.TokenService;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
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
