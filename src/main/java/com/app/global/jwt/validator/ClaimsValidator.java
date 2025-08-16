package com.app.global.jwt.validator;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.TokenType;
import com.app.global.util.ValidationUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ClaimsValidator {

    public void validateTokenType(String tokenType, TokenType expectedTokenType) {
        ValidationUtils.hasText(tokenType, ErrorType.INVALID_CLAIM);

        TokenType actualTokenType = TokenType.from(tokenType);
        if (actualTokenType.isDifferent(expectedTokenType)) {
            throw new AuthenticationException(ErrorType.INVALID_TOKEN_TYPE);
        }
    }

    public void validateExpiration(Date expirationDate) {
        ValidationUtils.notNull(expirationDate, ErrorType.INVALID_CLAIM);
    }

    public void validateMemberInfo(Long memberId, String role) {
        ValidationUtils.notNull(memberId, ErrorType.INVALID_CLAIM);
        ValidationUtils.hasText(role, ErrorType.INVALID_CLAIM);
    }
}
