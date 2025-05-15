package com.app.web.client;

import com.app.support.WireMockSupport;
import com.app.web.dto.request.KakaoTokenRequest;
import com.app.web.dto.response.KakaoTokenResponse;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import feign.FeignException;
import feign.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.SocketTimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
class KakaoTokenClientTest extends WireMockSupport {

    @Autowired
    private KakaoTokenClient kakaoTokenClient;

    @Value("${retry.max-attempts}")
    private Integer retryMaxAttempts;

    @Value("${spring.cloud.openfeign.client.config.default.readTimeout}")
    private Integer readTimeout;

    @DisplayName("스텁 서버에 카카오 토큰 발급 요청을 보내 미리 설정한 응답을 받는다.")
    @Test
    void requestKakaoToken() {
        // given
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile("request-kakao-token-response.json"))
        );

        KakaoTokenRequest request = createTestKakaoTokenRequest();

        // when
        KakaoTokenResponse response = kakaoTokenClient.requestKakaoToken(request);

        // then
        assertThat(response)
                .extracting("tokenType", "accessToken", "expiresIn", "refreshToken", "refreshTokenExpiresIn", "scope")
                .containsExactly("bearer", "access-token", 43199, "refresh-token", 5184000, "account-email profile-image profile-nickname");
    }

    @DisplayName("카카오 토큰 발급 요청 시 Not Found가 반환될 경우, 예외가 발생한다.")
    @Test
    void requestKakaoToken_NotFound() {
        // given
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.value()))
        );

        KakaoTokenRequest request = createTestKakaoTokenRequest();

        // when & then
        assertThatThrownBy(() -> kakaoTokenClient.requestKakaoToken(request))
                .isInstanceOf(FeignException.class);
    }

    @DisplayName("카카오 토큰 발급 요청 시 Internal Server Error가 반환될 경우, 예외가 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void requestKakaoToken_InternalServerError() {
        // given
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(INTERNAL_SERVER_ERROR.value()))
        );

        KakaoTokenRequest request = createTestKakaoTokenRequest();

        // when & then
        assertThatThrownBy(() -> kakaoTokenClient.requestKakaoToken(request))
                .isInstanceOf(RetryableException.class);

        verify(retryMaxAttempts, createTestRequestPatternBuilder());
    }

    @DisplayName("카카오 토큰 발급 요청 시 Read Timeout이 발생할 경우, 예외가 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void requestKakaoToken_ReadTimeout() {
        // given
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withFixedDelay(readTimeout + 1000))
        );

        KakaoTokenRequest request = createTestKakaoTokenRequest();
        Throwable throwable = catchThrowable(() -> kakaoTokenClient.requestKakaoToken(request));

        // when & then
        assertThat(throwable)
                .hasCauseInstanceOf(SocketTimeoutException.class)
                .isInstanceOf(RetryableException.class);

        verify(retryMaxAttempts, createTestRequestPatternBuilder());
    }

    private KakaoTokenRequest createTestKakaoTokenRequest() {
        return KakaoTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id("client-id")
                .redirect_uri("redirect-uri")
                .code("code")
                .client_secret("client-secret")
                .build();
    }

    private RequestPatternBuilder createTestRequestPatternBuilder() {
        return postRequestedFor(urlPathEqualTo("/oauth/token"))
                .withQueryParam("grant_type", equalTo("authorization_code"))
                .withQueryParam("client_id", equalTo("client-id"))
                .withQueryParam("redirect_uri", equalTo("redirect-uri"))
                .withQueryParam("code", equalTo("code"))
                .withQueryParam("client_secret", equalTo("client-secret"));
    }
}
