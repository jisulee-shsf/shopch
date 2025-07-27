package com.app.api.logout.service;

import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutService {

    private final MemberService memberService;

    public void logout(Long memberId, LocalDateTime logoutDateTime) {
        Member member = memberService.getMemberById(memberId);
        member.expireRefreshToken(logoutDateTime);
    }
}
