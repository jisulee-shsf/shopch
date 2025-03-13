package com.app.web.client;

import com.app.web.dto.KakaoTokenRequest;
import com.app.web.dto.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

import static com.app.global.config.FeignConfig.APPLICATION_FORM_URLENCODED_WITH_UTF8;

@FeignClient(name = "kakaoTokenClient", url = "${kakao-token.feign.url}")
public interface KakaoTokenClient {

    @PostMapping(value = "/oauth/token", consumes = APPLICATION_FORM_URLENCODED_WITH_UTF8)
    KakaoTokenResponse requestKakaoToken(@SpringQueryMap KakaoTokenRequest request);
}
