package com.app.web.controller;

import com.app.web.client.KakaoTokenClient;
import com.app.web.dto.request.KakaoTokenRequest;
import com.app.web.dto.response.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoTokenController {

    private final KakaoTokenClient kakaoTokenClient;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity<KakaoTokenResponse> loginCallback(String code) {
        KakaoTokenRequest request = KakaoTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id(clientId)
                .redirect_uri(redirectUri)
                .code(code)
                .client_secret(clientSecret)
                .build();
        return ResponseEntity.ok(kakaoTokenClient.requestKakaoToken(request));
    }
}
