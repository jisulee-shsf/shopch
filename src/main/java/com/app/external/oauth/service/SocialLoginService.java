package com.app.external.oauth.service;

import com.app.external.oauth.dto.response.SocialLoginUserInfoResponse;

public interface SocialLoginService {

    SocialLoginUserInfoResponse getUserInfo(String accessToken);
}
