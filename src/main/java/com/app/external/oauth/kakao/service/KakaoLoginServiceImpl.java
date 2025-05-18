package com.app.external.oauth.kakao.service;

import com.app.domain.member.constant.MemberType;
import com.app.external.oauth.dto.response.SocialLoginUserInfoResponse;
import com.app.external.oauth.kakao.client.KakaoUserInfoClient;
import com.app.external.oauth.kakao.dto.response.KakaoUserInfoResponse;
import com.app.external.oauth.service.SocialLoginService;
import com.app.global.jwt.constant.GrantType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class KakaoLoginServiceImpl implements SocialLoginService {

    private final KakaoUserInfoClient kakaoUserInfoClient;

    @Override
    public SocialLoginUserInfoResponse getUserInfo(String accessToken) {
        KakaoUserInfoResponse response = kakaoUserInfoClient.getKakaoUserInfo(GrantType.BEARER.getType() + " " + accessToken);
        KakaoUserInfoResponse.KakaoAccount account = response.getKakaoAccount();
        String email = account.getEmail();

        return SocialLoginUserInfoResponse.builder()
                .name(account.getProfile().getNickname())
                .email(StringUtils.hasText(email) ? email : String.valueOf(response.getId()))
                .profile(account.getProfile().getThumbnailImageUrl())
                .memberType(MemberType.KAKAO.name())
                .build();
    }
}
