package com.app.api.logout.controller;

import com.app.api.logout.service.LogoutService;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout(@MemberInfo MemberInfoDto memberInfo) {
        LocalDateTime logoutDateTime = LocalDateTime.now();
        logoutService.logout(memberInfo.getId(), logoutDateTime);
        return ResponseEntity.noContent().build();
    }
}
