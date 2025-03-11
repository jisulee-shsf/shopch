package com.app.web.client;

import com.app.web.dto.KakaoTokenRequest;
import com.app.web.dto.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.http.MediaType.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(name = "kakaoTokenClient", url = "${kakao-token.feign.url}")
public interface KakaoTokenClient {

    @PostMapping(value = "/oauth/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponse requestKakaoToken(KakaoTokenRequest request);
}
