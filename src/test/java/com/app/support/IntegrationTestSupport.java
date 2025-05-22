package com.app.support;

import com.app.external.oauth.kakao.client.KakaoUserInfoClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected KakaoUserInfoClient kakaoUserInfoClient;

    @MockitoSpyBean
    protected io.jsonwebtoken.Clock jwtClock;

    @MockitoSpyBean
    protected java.time.Clock clock;
}
