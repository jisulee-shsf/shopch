package com.app.external.oauth.kakao.service;

import com.app.external.oauth.dto.SocialLoginUserInfoResponse;
import com.app.external.oauth.kakao.client.KakaoUserInfoClient;
import com.app.external.oauth.kakao.dto.KakaoUserInfoResponse;
import com.app.external.oauth.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.global.jwt.constant.GrantType.BEARER;

@Service
@RequiredArgsConstructor
public class KakaoLoginServiceImpl implements SocialLoginService {

    private final KakaoUserInfoClient kakaoUserInfoClient;

    @Override
    public SocialLoginUserInfoResponse getUserInfo(String accessToken) {
        KakaoUserInfoResponse response = kakaoUserInfoClient.getKakaoUserInfo(BEARER.getType() + " " + accessToken);
        KakaoUserInfoResponse.KakaoAccount account = response.getKakaoAccount();
        String email = account.getEmail();

        return SocialLoginUserInfoResponse.builder()
                .name(account.getProfile().getNickname())
                .email(email)
                .profile(account.getProfile().getThumbnailImageUrl())
                .memberType(KAKAO)
                .build();
    }
}
