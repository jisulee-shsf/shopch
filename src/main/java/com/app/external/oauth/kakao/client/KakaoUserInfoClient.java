package com.app.external.oauth.kakao.client;

import com.app.external.oauth.kakao.dto.response.KakaoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoUserInfoClient", url = "${kakao-user-info.feign.url}")
public interface KakaoUserInfoClient {

    @GetMapping(value = "/v2/user/me")
    KakaoUserInfoResponse getKakaoUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
