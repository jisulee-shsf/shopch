package com.shopch.support;

import com.shopch.external.oauth.provider.kakao.client.KakaoTokenClient;
import com.shopch.external.oauth.provider.kakao.client.KakaoUserInfoClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected KakaoTokenClient kakaoTokenClient;

    @MockitoBean
    protected KakaoUserInfoClient kakaoUserInfoClient;

    @MockitoSpyBean
    protected java.time.Clock clock;
}
