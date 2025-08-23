package com.app.api.login.controller;

import com.app.api.login.controller.dto.request.LoginRequest;
import com.app.api.login.service.LoginService;
import com.app.api.login.service.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Date issuedAt = new Date();
        return ResponseEntity.ok(loginService.login(request.toServiceRequest(), issuedAt));
    }
}
