package com.app.external.oauth.service;

import com.app.domain.member.constant.OAuthType;
import com.app.external.oauth.dto.response.SocialLoginUserInfoResponse;

public interface SocialLoginService {

    SocialLoginUserInfoResponse getUserInfo(String accessToken);

    OAuthType getOauthType();
}
