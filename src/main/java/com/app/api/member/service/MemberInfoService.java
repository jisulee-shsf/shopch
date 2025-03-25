package com.app.api.member.service;

import com.app.api.member.dto.MemberInfoResponse;
import com.app.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInfoService {

    private final MemberService memberService;

    public MemberInfoResponse getMemberInfo(Long memberId) {
        return MemberInfoResponse.of(memberService.findMemberById(memberId));
    }
}
