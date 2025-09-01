package com.shopch.api.auth.service;

import com.shopch.api.auth.service.dto.request.LoginServiceRequest;
import com.shopch.api.auth.service.dto.response.LoginResponse;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.domain.member.constant.Role;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.service.MemberService;
import com.shopch.domain.token.service.RefreshTokenService;
import com.shopch.external.oauth.dto.UserInfo;
import com.shopch.external.oauth.service.SocialLoginService;
import com.shopch.external.oauth.service.SocialLoginServiceFactory;
import com.shopch.global.jwt.dto.TokenPair;
import com.shopch.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final SocialLoginServiceFactory socialLoginServiceFactory;
    private final JwtTokenProvider jwtProvider;

    public LoginResponse login(LoginServiceRequest request, Date issuedAt) {
        UserInfo userInfo = getUserInfoFromSocialLoginService(request.getOauthProvider(), request.getCode());
        Member member = getOrRegisterMember(userInfo);

        TokenPair tokenPair = jwtProvider.createTokenPair(member, issuedAt);
        updateOrRegisterRefreshToken(member, tokenPair);

        return LoginResponse.of(tokenPair);
    }

    private UserInfo getUserInfoFromSocialLoginService(OAuthProvider oauthProvider, String code) {
        SocialLoginService service = socialLoginServiceFactory.getSocialLoginService(oauthProvider);
        return service.getUserInfo(code);
    }

    private Member getOrRegisterMember(UserInfo userInfo) {
        return memberService.findActiveMember(userInfo.getOauthId(), userInfo.getOauthProvider())
                .orElseGet(() -> memberService.registerMember(userInfo.toMember(Role.USER)));
    }

    private void updateOrRegisterRefreshToken(Member member, TokenPair tokenPair) {
        refreshTokenService.findRefreshToken(member.getId())
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(tokenPair.getRefreshToken(), tokenPair.getRefreshTokenExpiresAt()),
                        () -> refreshTokenService.registerRefreshToken(tokenPair.toRefreshToken(member)));
    }

    public void deleteAccount(Long memberId, LocalDateTime deletedAt) {
        refreshTokenService.deleteRefreshToken(memberId);

        Member member = memberService.getMember(memberId);
        member.updateDeletedAt(deletedAt);
    }
}
