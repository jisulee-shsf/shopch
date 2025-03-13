package com.app.external.oauth.kakao.client;

import com.app.external.oauth.kakao.dto.KakaoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.app.global.config.FeignConfig.APPLICATION_FORM_URLENCODED_WITH_UTF8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "kakaoUserInfoClient", url = "${kakao-user-info.feign.url}")
public interface KakaoUserInfoClient {

    @GetMapping(value = "/v2/user/me", consumes = APPLICATION_FORM_URLENCODED_WITH_UTF8)
    KakaoUserInfoResponse getKakaoUserInfo(@RequestHeader(AUTHORIZATION) String accessToken);
}
