package com.app.api.token.service;

import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.global.jwt.constant.GrantType;
import com.app.global.jwt.service.TokenManager;
import com.app.global.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessTokenService {

    private final MemberService memberService;
    private final TokenManager tokenManager;

    public AccessTokenResponse createAccessTokenByRefreshToken(String refreshToken, Date reissueDate) {
        tokenManager.validateRefreshToken(refreshToken);

        Member member = memberService.getMemberByRefreshToken(refreshToken);
        Date accessTokenExpirationDate = tokenManager.createAccessTokenExpirationDate(reissueDate);
        String accessToken = tokenManager.createAccessToken(member.getId(), member.getRole(), reissueDate, accessTokenExpirationDate);

        return AccessTokenResponse.builder()
                .grantType(GrantType.BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(DateTimeUtils.convertDateToLocalDateTime(accessTokenExpirationDate))
                .build();
    }
}
