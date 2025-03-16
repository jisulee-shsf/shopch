package com.app.api.token.service;

import com.app.api.token.dto.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.global.jwt.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessTokenService {

    private final MemberService memberService;
    private final TokenManager tokenManager;

    public AccessTokenResponse createAccessTokenByRefreshToken(String refreshToken, Date reissueDate) {
        Member member = memberService.findMemberByRefreshToken(refreshToken);

        Date accessTokenExpirationDate = tokenManager.createAccessTokenExpirationDate(reissueDate);
        String accessToken = tokenManager.createAccessToken(member.getId(), member.getRole(), reissueDate, accessTokenExpirationDate);

        return AccessTokenResponse.builder()
                .grantType(BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(convertDateToLocalDateTime(accessTokenExpirationDate))
                .build();
    }
}
