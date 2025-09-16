package com.shopch.domain.member.service;

import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member registerMember(Member member) {
        return memberRepository.save(member);
    }

    public Optional<Member> findActiveMember(String oauthId, OAuthProvider oauthProvider) {
        return memberRepository.findByOauthIdAndOauthProvider(oauthId, oauthProvider);
    }

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
