package com.app.api.login.validator;

import com.app.global.error.exception.AuthenticationException;
import com.app.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.global.error.ErrorType.*;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OauthValidatorTest {

    private final OauthValidator oauthValidator = new OauthValidator();

    @DisplayName("Authorization 헤더를 검증한다.")
    @Test
    void validateAuthorizationHeader() {
        // given
        // when & then
        assertDoesNotThrow(() -> oauthValidator.validateAuthorizationHeader(BEARER.getType() + " access-token"));
    }

    @DisplayName("Authorization 헤더가 없을 때 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateAuthorizationHeader_MissingAuthorizationHeader() {
        // given
        // when & then
        assertThatThrownBy(() -> oauthValidator.validateAuthorizationHeader(null))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(MISSING_AUTHORIZATION_HEADER.getErrorMessage());
    }

    @DisplayName("Grant 타입이 없을 때 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateAuthorizationHeader_MissingGrantType() {
        // given
        // when & then
        assertThatThrownBy(() -> oauthValidator.validateAuthorizationHeader("access-token"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_GRANT_TYPE.getErrorMessage());
    }

    @DisplayName("Grant 타입이 유효하지 않을 때 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateAuthorizationHeader_InvalidGrantType() {
        // given
        // when & then
        assertThatThrownBy(() -> oauthValidator.validateAuthorizationHeader("invalid-grant-type access-token"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_GRANT_TYPE.getErrorMessage());
    }

    @DisplayName("Member 타입을 검증한다.")
    @Test
    void validateMemberType() {
        // given
        // when & then
        assertDoesNotThrow(() -> oauthValidator.validateMemberType(KAKAO.name()));
    }

    @DisplayName("Member 타입이 유효하지 않을 때 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateMemberType_InvalidMemberType() {
        // given
        // when & then
        assertThatThrownBy(() -> oauthValidator.validateMemberType("invalid-member-type"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(INVALID_MEMBER_TYPE.getErrorMessage());
    }
}
