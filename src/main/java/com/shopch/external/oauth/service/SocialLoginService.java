package com.shopch.external.oauth.service;

import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.external.oauth.dto.UserInfo;

public interface SocialLoginService {

    OAuthProvider oauthProvider();

    UserInfo getUserInfo(String code);
}
