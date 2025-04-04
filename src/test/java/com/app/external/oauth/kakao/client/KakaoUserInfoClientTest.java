package com.app.external.oauth.kakao.client;

import com.app.external.oauth.kakao.dto.KakaoUserInfoResponse;
import com.app.support.WireMockSupport;
import feign.FeignException;
import feign.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.SocketTimeoutException;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
class KakaoUserInfoClientTest extends WireMockSupport {

    @Autowired
    private KakaoUserInfoClient kakaoUserInfoClient;

    @Value("${retry.max-attempts}")
    private Integer retryMaxAttempts;

    @Value("${spring.cloud.openfeign.client.config.default.readTimeout}")
    private Integer readTimeout;

    @DisplayName("스텁 서버에 카카오 사용자 정보 요청을 보내 미리 설정한 응답을 받는다.")
    @Test
    void getKakaoUserInfo() {
        // given
        stubFor(get(urlEqualTo("/v2/user/me"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile("get-kakao-user-info-response.json"))
        );

        // when
        KakaoUserInfoResponse response =
                kakaoUserInfoClient.getKakaoUserInfo(BEARER.getType() + " access-token");

        // then
        assertThat(response)
                .extracting("id", "kakaoAccount.email", "kakaoAccount.profile.nickname", "kakaoAccount.profile.thumbnailImageUrl")
                .containsExactly(1L, "member@email.com", "member", "http://img1.kakaocdn.net/.../thumbnail.jpeg");
    }

    @DisplayName("카카오 사용자 정보 요청 시 Not Found가 반환될 경우, 예외가 발생한다.")
    @Test
    void getKakaoUserInfo_NotFound() {
        // given
        stubFor(get(urlEqualTo("/v2/user/me"))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.value()))
        );

        // when & then
        assertThatThrownBy(() -> kakaoUserInfoClient.getKakaoUserInfo(BEARER.getType() + " access-token"))
                .isInstanceOf(FeignException.class);
    }

    @DisplayName("카카오 사용자 정보 요청 시 Internal Server Error가 반환될 경우, 예외가 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void getKakaoUserInfo_InternalServerError() {
        // given
        stubFor(get(urlEqualTo("/v2/user/me"))
                .willReturn(aResponse()
                        .withStatus(INTERNAL_SERVER_ERROR.value()))
        );

        // when & then
        assertThatThrownBy(() -> kakaoUserInfoClient.getKakaoUserInfo(BEARER.getType() + " access-token"))
                .isInstanceOf(RetryableException.class);

        verify(retryMaxAttempts, getRequestedFor(urlEqualTo("/v2/user/me")));
    }

    @DisplayName("카카오 사용자 정보 요청 시 Read Timeout이 발생할 경우, 예외가 발생하고 최대 횟수만큼 요청을 재시도한다.")
    @Test
    void getKakaoUserInfo_ReadTimeout() {
        // given
        stubFor(get(urlEqualTo("/v2/user/me"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withFixedDelay(readTimeout + 1000))
        );

        // when
        Throwable throwable = catchThrowable(() -> kakaoUserInfoClient.getKakaoUserInfo(BEARER.getType() + " access-token"));

        // then
        assertThat(throwable)
                .hasCauseInstanceOf(SocketTimeoutException.class)
                .isInstanceOf(RetryableException.class);

        verify(retryMaxAttempts, getRequestedFor(urlEqualTo("/v2/user/me")));
    }
}
