package com.app.api.login.service;

import com.app.api.login.service.dto.request.OauthLoginServiceRequest;
import com.app.api.login.service.dto.response.OauthLoginResponse;
import com.app.domain.member.constant.MemberType;
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
public class OauthLoginService {

    private final MemberRepository memberRepository;
    private final TokenManager tokenManager;

    public OauthLoginResponse oauthLogin(OauthLoginServiceRequest request, String accessToken, Date issueDate) {
        SocialLoginUserInfoResponse userInfoResponse = getUserInfoFromSocialLoginService(request.getMemberType(), accessToken);

        Member member = findOrSaveMember(userInfoResponse);
        TokenResponse tokenResponse = tokenManager.createToken(member.getId(), member.getRole(), issueDate);
        member.updateRefreshToken(tokenResponse.getRefreshToken(), tokenResponse.getRefreshTokenExpirationDateTime());
        return OauthLoginResponse.of(tokenResponse);
    }

    private SocialLoginUserInfoResponse getUserInfoFromSocialLoginService(MemberType memberType, String accessToken) {
        SocialLoginService service = SocialLoginServiceFactory.getSocialLoginService(memberType);
        return service.getUserInfo(accessToken);
    }

    private Member findOrSaveMember(SocialLoginUserInfoResponse response) {
        return memberRepository.findByEmail(response.getEmail())
                .orElseGet(() -> memberRepository.save(response.toEntity(Role.USER)));
    }
}
