package com.shopch.api.member.controller;

import com.shopch.api.member.service.MemberApiService;
import com.shopch.api.member.service.dto.MemberInfoResponse;
import com.shopch.global.resolver.MemberInfo;
import com.shopch.global.resolver.dto.MemberInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberApiService memberApiService;

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@MemberInfo MemberInfoDto memberInfo) {
        return ResponseEntity.ok(memberApiService.getMemberInfo(memberInfo.getId()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@MemberInfo MemberInfoDto memberInfo) {
        LocalDateTime deletedAt = LocalDateTime.now();
        memberApiService.deleteMember(memberInfo.getId(), deletedAt);
        return ResponseEntity.noContent().build();
    }
}
