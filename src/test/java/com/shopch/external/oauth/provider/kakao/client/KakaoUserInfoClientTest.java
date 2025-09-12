package com.shopch.external.oauth.provider.kakao.client;

import com.shopch.external.oauth.provider.kakao.dto.response.KakaoUserInfoResponse;
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
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class KakaoUserInfoClientTest extends WireMockSupport {

    private static final String RESPONSE_FILE_NAME = "kakao-user-info-response.json";
    private static final String OAUTH_ID = "1";
    private static final String USER_NAME = "user";
    private static final String USER_EMAIL = "user@email.com";
    private static final String USER_IMAGE_URL = "http://yyy.kakao.com/.../img_110x110.jpg";

    @Value("${oauth.provider.kakao.user-info-client.path}")
    private String kakaoUserInfoClientPath;

    @Value("${retry.max-attempts}")
    private int retryMaxAttempts;

    @Value("${spring.cloud.openfeign.client.config.default.readTimeout}")
    private int readTimeout;

    @Autowired
    private KakaoUserInfoClient kakaoUserInfoClient;

    @DisplayName("카카오 사용자 정보를 요청해 설정한 응답을 받는다.")
    @Test
    void getKakaoUserInfo() {
        // given
        stubFor(get(urlEqualTo(kakaoUserInfoClientPath))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile(RESPONSE_FILE_NAME))
        );

        // when
        KakaoUserInfoResponse response = kakaoUserInfoClient.getKakaoUserInfo(BEARER.getPrefix() + ACCESS_TOKEN);

        // then
        assertThat(response)
                .extracting(
                        KakaoUserInfoResponse::getOauthId,
                        KakaoUserInfoResponse::getName,
                        KakaoUserInfoResponse::getEmail,
                        KakaoUserInfoResponse::getImageUrl)
                .containsExactly(
                        OAUTH_ID,
                        USER_NAME,
                        USER_EMAIL,
                        USER_IMAGE_URL
                );
    }

    @DisplayName("카카오 사용자 정보를 요청할 때 Not Found가 반환될 경우, FeignException이 발생한다.")
    @Test
    void getKakaoUserInfo_NotFound() {
        // given
        stubFor(get(urlEqualTo(kakaoUserInfoClientPath))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.value()))
        );

        // when & then
        assertThatThrownBy(() -> kakaoUserInfoClient.getKakaoUserInfo(BEARER.getPrefix() + ACCESS_TOKEN))
                .isInstanceOf(FeignException.NotFound.class);
    }

    @DisplayName("카카오 사용자 정보를 요청할 때 Internal Server Error가 반환될 경우, RetryableException이 발생하고 최대 시도 횟수만큼 요청을 재시도한다.")
    @Test
    void getKakaoUserInfo_InternalServerError() {
        // given
        stubFor(get(urlEqualTo(kakaoUserInfoClientPath))
                .willReturn(aResponse()
                        .withStatus(INTERNAL_SERVER_ERROR.value()))
        );

        // when & then
        assertThatThrownBy(() -> kakaoUserInfoClient.getKakaoUserInfo(BEARER.getPrefix() + ACCESS_TOKEN))
                .isInstanceOf(RetryableException.class)
                .hasCauseInstanceOf(FeignException.InternalServerError.class);

        verify(retryMaxAttempts, getRequestedFor(urlEqualTo(kakaoUserInfoClientPath)));
    }

    @DisplayName("카카오 사용자 정보를 요청할 때 Read Timeout이 발생할 경우, RetryableException이 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void getKakaoUserInfo_ReadTimeout() {
        // given
        stubFor(get(urlEqualTo(kakaoUserInfoClientPath))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withFixedDelay(readTimeout + ONE_SECOND_IN_MILLIS))
        );

        // when & then
        assertThatThrownBy(() -> kakaoUserInfoClient.getKakaoUserInfo(BEARER.getPrefix() + ACCESS_TOKEN))
                .isInstanceOf(RetryableException.class)
                .hasCauseInstanceOf(SocketTimeoutException.class);

        verify(retryMaxAttempts, getRequestedFor(urlEqualTo(kakaoUserInfoClientPath)));
    }
}
