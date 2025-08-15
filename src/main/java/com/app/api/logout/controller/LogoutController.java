package com.app.api.logout.controller;

import com.app.api.logout.service.LogoutService;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout(@MemberInfo MemberInfoDto memberInfo) {
        logoutService.logout(memberInfo.getId());
        return ResponseEntity.noContent().build();
    }
}
