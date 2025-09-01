package com.shopch.api.auth.controller;

import com.shopch.api.auth.controller.dto.LoginRequest;
import com.shopch.api.auth.service.LoginService;
import com.shopch.api.auth.service.dto.response.LoginResponse;
import com.shopch.global.resolver.MemberInfo;
import com.shopch.global.resolver.dto.MemberInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Date issuedAt = new Date();
        return ResponseEntity.ok(loginService.login(request.toServiceRequest(), issuedAt));
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(@MemberInfo MemberInfoDto memberInfo) {
        LocalDateTime deletedAt = LocalDateTime.now();
        loginService.deleteAccount(memberInfo.getId(), deletedAt);
        return ResponseEntity.noContent().build();
    }
}
