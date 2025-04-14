package com.app.api.logout.controller;

import com.app.api.logout.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.app.global.util.AuthorizationHeaderUtils.validateAuthorizationHeader;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);

        String accessToken = authorizationHeader.split(" ")[1];
        logoutService.logout(accessToken, LocalDateTime.now());

        return ResponseEntity.ok().build();
    }
}
