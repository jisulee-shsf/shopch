package com.app.web.client;

import com.app.global.config.FeignConfig;
import com.app.web.dto.request.KakaoTokenRequest;
import com.app.web.dto.response.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "kakaoTokenClient", url = "${kakao-token.feign.url}")
public interface KakaoTokenClient {

    @PostMapping(value = "/oauth/token", consumes = FeignConfig.APPLICATION_FORM_URLENCODED_WITH_UTF8)
    KakaoTokenResponse requestKakaoToken(@SpringQueryMap KakaoTokenRequest request);
}
