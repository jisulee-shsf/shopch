package com.app.external.oauth.kakao.client;

import com.app.external.oauth.kakao.dto.response.KakaoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoUserInfoClient", url = "${oauth.provider.kakao.user-info-client.url}")
public interface KakaoUserInfoClient {

    @GetMapping(value = "${oauth.provider.kakao.user-info-client.path}")
    KakaoUserInfoResponse getKakaoUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
