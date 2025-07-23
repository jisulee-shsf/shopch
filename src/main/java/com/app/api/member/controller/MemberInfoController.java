package com.app.api.member.controller;

import com.app.api.member.service.MemberInfoService;
import com.app.api.member.service.dto.response.MemberInfoResponse;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    @GetMapping("/api/member/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@MemberInfo MemberInfoDto memberInfo) {
        return ResponseEntity.ok(memberInfoService.getMemberInfo(memberInfo.getId()));
    }
}
