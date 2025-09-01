package com.shopch.api.auth.controller;

import com.shopch.api.auth.service.LogoutService;
import com.shopch.global.resolver.MemberInfo;
import com.shopch.global.resolver.dto.MemberInfoDto;
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
