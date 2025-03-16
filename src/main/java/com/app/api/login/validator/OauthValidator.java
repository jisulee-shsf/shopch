package com.app.api.login.validator;

import com.app.domain.member.constant.MemberType;
import com.app.global.error.exception.AuthenticationException;
import org.springframework.stereotype.Service;

import static com.app.global.error.ErrorType.INVALID_MEMBER_TYPE;

@Service
public class OauthValidator {

    public void validateMemberType(String memberType) {
        if (!MemberType.isMemberType(memberType)) {
            throw new AuthenticationException(INVALID_MEMBER_TYPE);
        }
    }
}
