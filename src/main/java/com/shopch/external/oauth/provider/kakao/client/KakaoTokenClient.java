package com.shopch.external.oauth.provider.kakao.client;

import com.shopch.external.oauth.provider.kakao.dto.request.KakaoTokenRequest;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "kakaoTokenClient", url = "${oauth.provider.kakao.token-client.url}")
public interface KakaoTokenClient {

    @PostMapping("${oauth.provider.kakao.token-client.path}")
    KakaoTokenResponse requestKakaoToken(@SpringQueryMap KakaoTokenRequest request);
}
