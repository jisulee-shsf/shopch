package com.shopch.api.auth.controller;

import com.shopch.api.auth.controller.dto.LoginRequest;
import com.shopch.api.auth.controller.dto.RefreshAccessTokenRequest;
import com.shopch.api.auth.service.AuthService;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
import com.shopch.api.auth.service.dto.response.LoginResponse;
import com.shopch.global.resolver.MemberInfo;
import com.shopch.global.resolver.dto.MemberInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/oauth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Date issuedAt = new Date();
        return ResponseEntity.ok(authService.login(request.toServiceRequest(), issuedAt));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@Valid @RequestBody RefreshAccessTokenRequest request) {
        Date issuedAt = new Date();
        return ResponseEntity.ok(authService.refreshAccessToken(request.toServiceRequest(), issuedAt));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@MemberInfo MemberInfoDto memberInfo) {
        authService.logout(memberInfo.getId());
        return ResponseEntity.noContent().build();
    }
}
