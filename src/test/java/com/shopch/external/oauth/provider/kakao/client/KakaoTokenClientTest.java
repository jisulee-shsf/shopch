package com.shopch.external.oauth.provider.kakao.client;

import com.shopch.external.oauth.provider.kakao.dto.request.KakaoTokenRequest;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoTokenResponse;
import com.shopch.support.WireMockSupport;
import feign.FeignException;
import feign.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.SocketTimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.shopch.fixture.TimeFixture.ONE_SECOND_IN_MILLIS;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class KakaoTokenClientTest extends WireMockSupport {

    private static final String RESPONSE_FILE_NAME = "kakao-token-response.json";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CLIENT_ID = "client_id";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String CODE = "code";
    private static final String CLIENT_SECRET = "client_secret";

    @Value("${oauth.provider.kakao.token-client.path}")
    private String kakaoTokenClientPath;

    @Value("${retry.max-attempts}")
    private int retryMaxAttempts;

    @Value("${spring.cloud.openfeign.client.config.default.readTimeout}")
    private int readTimeout;

    @Autowired
    private KakaoTokenClient kakaoTokenClient;

    @DisplayName("카카오 토큰을 요청해 설정한 응답을 받는다.")
    @Test
    void requestKakaoToken() {
        // given
        stubFor(post(urlPathEqualTo(kakaoTokenClientPath))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile(RESPONSE_FILE_NAME))
        );

        KakaoTokenRequest request = createKakaoTokenRequest();

        // when
        KakaoTokenResponse response = kakaoTokenClient.requestKakaoToken(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @DisplayName("카카오 토큰을 요청할 때 Not Found가 반환될 경우, FeignException이 발생한다.")
    @Test
    void requestKakaoToken_NotFound() {
        // given
        stubFor(post(urlPathEqualTo(kakaoTokenClientPath))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.value()))
        );

        KakaoTokenRequest request = createKakaoTokenRequest();

        // when & then
        assertThatThrownBy(() -> kakaoTokenClient.requestKakaoToken(request))
                .isInstanceOf(FeignException.NotFound.class);
    }

    @DisplayName("카카오 토큰을 요청할 때 Internal Server Error가 반환될 경우, RetryableException이 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void requestKakaoToken_InternalServerError() {
        // given
        stubFor(post(urlPathEqualTo(kakaoTokenClientPath))
                .willReturn(aResponse()
                        .withStatus(INTERNAL_SERVER_ERROR.value()))
        );

        KakaoTokenRequest request = createKakaoTokenRequest();

        // when & then
        assertThatThrownBy(() -> kakaoTokenClient.requestKakaoToken(request))
                .isInstanceOf(RetryableException.class)
                .hasCauseInstanceOf(FeignException.InternalServerError.class);

        verify(retryMaxAttempts, postRequestedFor(urlPathEqualTo(kakaoTokenClientPath)));
    }

    @DisplayName("카카오 토큰을 요청할 때 Read Timeout이 발생할 경우, RetryableException이 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void requestKakaoToken_ReadTimeout() {
        // given
        stubFor(post(urlPathEqualTo(kakaoTokenClientPath))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withFixedDelay(readTimeout + ONE_SECOND_IN_MILLIS))
        );

        KakaoTokenRequest request = createKakaoTokenRequest();

        // when & then
        assertThatThrownBy(() -> kakaoTokenClient.requestKakaoToken(request))
                .isInstanceOf(RetryableException.class)
                .hasCauseInstanceOf(SocketTimeoutException.class);

        verify(retryMaxAttempts, postRequestedFor(urlPathEqualTo(kakaoTokenClientPath)));
    }

    private KakaoTokenRequest createKakaoTokenRequest() {
        return KakaoTokenRequest.builder()
                .grantType(GRANT_TYPE)
                .clientId(CLIENT_ID)
                .redirectUri(REDIRECT_URI)
                .code(CODE)
                .clientSecret(CLIENT_SECRET)
                .build();
    }
}
