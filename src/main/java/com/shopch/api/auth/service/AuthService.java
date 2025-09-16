package com.shopch.api.auth.service;

import com.shopch.api.auth.service.dto.request.OAuthLoginServiceRequest;
import com.shopch.api.auth.service.dto.request.RefreshAccessTokenServiceRequest;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
import com.shopch.api.auth.service.dto.response.OAuthLoginResponse;
import com.shopch.domain.member.constant.Role;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.service.MemberService;
import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.domain.token.service.RefreshTokenService;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.external.oauth.dto.UserInfo;
import com.shopch.external.oauth.service.SocialLoginService;
import com.shopch.external.oauth.service.SocialLoginServiceFactory;
import com.shopch.global.auth.JwtProvider;
import com.shopch.global.auth.dto.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final SocialLoginServiceFactory socialLoginServiceFactory;
    private final JwtProvider jwtProvider;

    @Transactional
    public OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest request, Instant issuedAt) {
        UserInfo userInfo = getUserInfoFromSocialLoginService(request.getOauthProvider(), request.getCode());
        Member member = getOrRegisterMember(userInfo);

        TokenPair tokenPair = jwtProvider.createTokenPair(member, issuedAt);
        updateOrRegisterRefreshToken(member, tokenPair);

        return OAuthLoginResponse.of(tokenPair);
    }

    public AccessTokenResponse refreshAccessToken(RefreshAccessTokenServiceRequest request, Instant issuedAt) {
        String refreshToken = request.getRefreshToken();
        jwtProvider.validateRefreshToken(refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshToken(refreshToken);
        Member member = refreshTokenEntity.getMember();

        String accessToken = jwtProvider.createAccessToken(member, issuedAt);
        LocalDateTime accessTokenExpiresAt = jwtProvider.getExpirationFrom(accessToken);

        return AccessTokenResponse.of(accessToken, accessTokenExpiresAt);
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenService.deleteRefreshToken(memberId);
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
                        refreshToken -> refreshToken.updateTokenInfo(tokenPair.getRefreshToken(), tokenPair.getRefreshTokenExpiresAt()),
                        () -> refreshTokenService.registerRefreshToken(tokenPair.toRefreshToken(member)));
    }
}
