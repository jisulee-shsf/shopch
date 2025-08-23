package com.app.external.oauth.service;

import com.app.domain.member.constant.OAuthProvider;
import com.app.external.oauth.dto.response.UserInfo;

public interface SocialLoginService {

    OAuthProvider oauthProvider();

    UserInfo getUserInfo(String code);
}
