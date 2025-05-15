package com.app.api.login.service;

import com.app.api.login.dto.OauthLoginResponse;
import com.app.domain.member.constant.MemberType;
import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.external.oauth.dto.SocialLoginUserInfoResponse;
import com.app.external.oauth.service.SocialLoginService;
import com.app.external.oauth.service.SocialLoginServiceFactory;
import com.app.global.jwt.dto.TokenResponse;
import com.app.global.jwt.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OauthLoginService {

    private final MemberService memberService;
    private final TokenManager tokenManager;

    public OauthLoginResponse oauthLogin(MemberType memberType, String accessToken, Date issueDate) {
        SocialLoginUserInfoResponse userInfoResponse = getUserInfoFromSocialLoginService(memberType, accessToken);
        Member oauthMember = getOrRegisterMember(userInfoResponse);

        TokenResponse tokenResponse = tokenManager.createToken(oauthMember.getId(), oauthMember.getRole(), issueDate);
        oauthMember.updateRefreshToken(tokenResponse.getRefreshToken(), tokenResponse.getRefreshTokenExpirationDateTime());

        return OauthLoginResponse.of(tokenResponse);
    }

    private SocialLoginUserInfoResponse getUserInfoFromSocialLoginService(MemberType memberType, String accessToken) {
        SocialLoginService service = SocialLoginServiceFactory.getSocialLoginService(memberType);
        return service.getUserInfo(accessToken);
    }

    private Member getOrRegisterMember(SocialLoginUserInfoResponse response) {
        Optional<Member> optionalMember = memberService.findMemberByEmail(response.getEmail());
        if (optionalMember.isPresent()) {
            return optionalMember.get();
        } else {
            Member member = response.toEntity(Role.USER);
            memberService.registerMember(member);
            return member;
        }
    }
}
