package com.shopch.global.auth;

import com.shopch.global.error.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static com.shopch.global.error.ErrorCode.INVALID_AUTHORIZATION_HEADER;
import static com.shopch.global.error.ErrorCode.MISSING_AUTHORIZATION_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

class BearerTokenExtractorTest {

    private static final String TOKEN = "token";
    private static final String INVALID_PREFIX = "Invalid ";

    private static Stream<String> blankStringProvider() {
        return Stream.of("", " ");
    }

    private final BearerTokenExtractor bearerTokenExtractor = new BearerTokenExtractor();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @DisplayName("Authorization 헤더에서 토큰을 추출한다.")
    @Test
    void extractToken() {
        // given
        given(request.getHeader(AUTHORIZATION))
                .willReturn(BEARER.getPrefix() + TOKEN);

        // when
        String token = bearerTokenExtractor.extractToken(request);

        // then
        assertThat(token).isEqualTo(TOKEN);
    }

    @DisplayName("Authorization 헤더가 없을 때 추출을 시도할 경우, 예외가 발생한다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("blankStringProvider")
    void extractToken_MissingAuthorizationHeader(String input) {
        // given
        given(request.getHeader(AUTHORIZATION))
                .willReturn(input);

        // when & then
        assertThatThrownBy(() -> bearerTokenExtractor.extractToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(MISSING_AUTHORIZATION_HEADER.getMessage());
    }

    @DisplayName("인증 스킴이 Bearer이 아닐 경우, 예외가 발생한다.")
    @Test
    void extractToken_InvalidAuthorizationHeader() {
        // given
        given(request.getHeader(AUTHORIZATION))
                .willReturn(INVALID_PREFIX + TOKEN);

        // when & then
        assertThatThrownBy(() -> bearerTokenExtractor.extractToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_AUTHORIZATION_HEADER.getMessage());
    }
}
