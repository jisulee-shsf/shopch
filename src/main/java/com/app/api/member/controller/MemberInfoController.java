package com.app.api.member.controller;

import com.app.api.member.dto.MemberInfoResponse;
import com.app.api.member.service.MemberInfoService;
import com.app.global.jwt.service.TokenManager;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberInfoService memberInfoService;
    private final TokenManager tokenManager;

    @GetMapping("/member/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@RequestHeader(AUTHORIZATION) String authorizationHeader) {
        String accessToken = authorizationHeader.split(" ")[1];
        Claims claims = tokenManager.getTokenClaims(accessToken);
        Long memberId = claims.get("memberId", Long.class);
        MemberInfoResponse response = memberInfoService.getMemberInfo(memberId);

        return ResponseEntity.ok(response);
    }
}
