package com.app.api.login.validator;

import com.app.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.global.error.ErrorType.INVALID_MEMBER_TYPE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OauthValidatorTest {

    private final OauthValidator oauthValidator = new OauthValidator();

    @DisplayName("회원 타입을 검증한다.")
    @Test
    void validateMemberType() {
        // given
        String memberType = KAKAO.name();

        // when & then
        assertDoesNotThrow(() -> oauthValidator.validateMemberType(memberType));
    }

    @DisplayName("회원 타입이 유효하지 않을 때 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateMemberType_InvalidMemberType() {
        // given
        String invalidMemberType = "NAVER";

        // when & then
        assertThatThrownBy(() -> oauthValidator.validateMemberType(invalidMemberType))
                .isInstanceOf(BusinessException.class)
                .hasMessage(INVALID_MEMBER_TYPE.getErrorMessage());
    }
}
