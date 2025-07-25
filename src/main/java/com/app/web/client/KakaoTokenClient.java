package com.app.web.client;

import com.app.web.dto.request.KakaoTokenRequest;
import com.app.web.dto.response.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "kakaoTokenClient", url = "${kakao-token.feign.url}")
public interface KakaoTokenClient {

    @PostMapping(value = "/oauth/token")
    KakaoTokenResponse requestKakaoToken(@SpringQueryMap KakaoTokenRequest request);
}
