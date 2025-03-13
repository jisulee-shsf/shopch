package com.app.external.oauth.service;

import com.app.external.oauth.dto.SocialLoginUserInfoResponse;

public interface SocialLoginService {

    SocialLoginUserInfoResponse getUserInfo(String accessToken);
}
