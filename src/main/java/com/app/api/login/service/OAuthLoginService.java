package com.app.api.login.service;

import com.app.api.login.service.dto.request.OAuthLoginServiceRequest;
import com.app.api.login.service.dto.response.OAuthLoginResponse;
import com.app.domain.member.constant.OAuthType;
import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.external.oauth.dto.response.SocialLoginUserInfoResponse;
import com.app.external.oauth.service.SocialLoginService;
import com.app.external.oauth.service.SocialLoginServiceFactory;
import com.app.global.jwt.dto.TokenResponse;
import com.app.global.jwt.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuthLoginService {

    private final MemberRepository memberRepository;
    private final SocialLoginServiceFactory socialLoginServiceFactory;
    private final TokenManager tokenManager;

    public OAuthLoginResponse oauthLogin(OAuthLoginServiceRequest request, String accessToken, Date issueDate) {
        SocialLoginUserInfoResponse userInfoResponse = getUserInfoFromSocialLoginService(request.getOAuthType(), accessToken);

        Member member = findOrSaveMember(userInfoResponse);
        TokenResponse tokenResponse = tokenManager.createTokenResponse(member.getId(), member.getRole(), issueDate);
        member.updateRefreshToken(tokenResponse.getRefreshToken(), tokenResponse.getRefreshTokenExpirationDateTime());
        return OAuthLoginResponse.of(tokenResponse);
    }

    private SocialLoginUserInfoResponse getUserInfoFromSocialLoginService(OAuthType oauthtype, String accessToken) {
        SocialLoginService service = socialLoginServiceFactory.getSocialLoginService(oauthtype);
        return service.getUserInfo(accessToken);
    }

    private Member findOrSaveMember(SocialLoginUserInfoResponse response) {
        return memberRepository.findByEmail(response.getEmail())
                .orElseGet(() -> memberRepository.save(response.toEntity(Role.USER)));
    }
}
