package com.app.global.jwt.validator;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.TokenType;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ClaimsValidator {

    public void validateTokenType(String tokenType, TokenType expectedTokenType) {
        validateNotNullOrBlank(tokenType);

        TokenType actualTokenType = TokenType.from(tokenType);
        if (actualTokenType.isDifferent(expectedTokenType)) {
            throw new AuthenticationException(ErrorType.TOKEN_TYPE_MISMATCH);
        }
    }

    public void validateExpiration(Date expirationDate) {
        validateNotNull(expirationDate);
    }

    public void validateMemberInfo(Long memberId, String role) {
        validateMemberId(memberId);
        validateRole(role);
    }

    private void validateNotNullOrBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new AuthenticationException(ErrorType.INVALID_CLAIM);
        }
    }

    private void validateNotNull(Object value) {
        if (value == null) {
            throw new AuthenticationException(ErrorType.INVALID_CLAIM);
        }
    }

    private void validateMemberId(Long memberId) {
        validateNotNull(memberId);
    }

    private void validateRole(String role) {
        validateNotNullOrBlank(role);
    }
}
