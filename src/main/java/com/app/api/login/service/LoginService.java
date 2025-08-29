package com.app.api.login.service;

import com.app.api.login.service.dto.request.LoginServiceRequest;
import com.app.api.login.service.dto.response.LoginResponse;
import com.app.domain.member.constant.OAuthProvider;
import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.domain.token.service.RefreshTokenService;
import com.app.external.oauth.dto.response.UserInfo;
import com.app.external.oauth.service.SocialLoginService;
import com.app.external.oauth.service.SocialLoginServiceFactory;
import com.app.global.jwt.dto.TokenPair;
import com.app.global.jwt.service.JwtProvider;
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
    private final JwtProvider jwtProvider;

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
