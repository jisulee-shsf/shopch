package com.app.api.member.controller;

import com.app.api.member.dto.MemberInfoResponse;
import com.app.api.member.service.MemberInfoService;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    @GetMapping("/member/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@MemberInfo MemberInfoRequest request) {
        return ResponseEntity.ok(memberInfoService.getMemberInfo(request.getId()));
    }
}
