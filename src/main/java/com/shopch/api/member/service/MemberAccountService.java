package com.shopch.api.member.service;

import com.shopch.api.member.service.dto.MemberInfoResponse;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.service.MemberService;
import com.shopch.domain.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAccountService {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    public MemberInfoResponse getMemberInfo(Long memberId) {
        return MemberInfoResponse.of(memberService.getMember(memberId));
    }

    @Transactional
    public void deleteMember(Long memberId, LocalDateTime deletedAt) {
        refreshTokenService.deleteRefreshToken(memberId);

        Member member = memberService.getMember(memberId);
        member.updateDeletedAt(deletedAt);
    }
}
