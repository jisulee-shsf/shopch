package com.app.domain.member.service;

import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.error.exception.BusinessException;
import com.app.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.app.global.error.ErrorType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final Clock clock;

    @Transactional
    public Long registerMember(Member member) {
        validateDuplicateMember(member);
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findMemberByRefreshToken(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
        validateRefreshTokenExpirationDateTime(member.getRefreshTokenExpirationDateTime());
        return member;
    }

    private void validateDuplicateMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new BusinessException(ALREADY_REGISTERED_MEMBER);
        }
    }

    private void validateRefreshTokenExpirationDateTime(LocalDateTime refreshTokenExpirationDateTime) {
        if (refreshTokenExpirationDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new AuthenticationException(EXPIRED_REFRESH_TOKEN);
        }
    }
}
