package com.app.api.login.validator;

import com.app.domain.member.constant.MemberType;
import com.app.global.error.exception.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.app.global.error.ErrorType.*;
import static com.app.global.jwt.constant.GrantType.BEARER;

@Service
public class OauthValidator {

    public static void validateAuthorizationHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new AuthenticationException(MISSING_AUTHORIZATION_HEADER);
        }

        String[] authorizations = authorizationHeader.split(" ");
        if (authorizations.length < 2 || !authorizations[0].equals(BEARER.getType())) {
            throw new AuthenticationException(INVALID_GRANT_TYPE);
        }
    }

    public static void validateMemberType(String memberType) {
        if (!MemberType.isMemberType(memberType)) {
            throw new AuthenticationException(INVALID_MEMBER_TYPE);
        }
    }
}
