package com.app.support;

import com.app.api.health.controller.HealthCheckController;
import com.app.api.login.controller.OauthLoginController;
import com.app.api.login.service.OauthLoginService;
import com.app.api.logout.controller.LogoutController;
import com.app.api.logout.service.LogoutService;
import com.app.api.member.controller.MemberInfoController;
import com.app.api.member.service.MemberInfoService;
import com.app.api.order.controller.OrderController;
import com.app.api.order.service.OrderService;
import com.app.api.product.controller.ProductController;
import com.app.api.product.service.ProductService;
import com.app.api.token.controller.TokenController;
import com.app.api.token.service.TokenService;
import com.app.web.client.KakaoTokenClient;
import com.app.web.controller.KakaoTokenController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@WebMvcTest(
        controllers = {
                HealthCheckController.class,
                KakaoTokenController.class,
                OauthLoginController.class,
                MemberInfoController.class,
                TokenController.class,
                LogoutController.class,
                ProductController.class,
                OrderController.class

        },
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {
                        WebMvcConfigurer.class,
                        HandlerInterceptor.class,
                        HandlerMethodArgumentResolver.class
                }
        )
)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected KakaoTokenClient kakaoTokenClient;

    @MockitoBean
    protected OauthLoginService oauthLoginService;

    @MockitoBean
    protected MemberInfoService memberInfoService;

    @MockitoBean
    protected TokenService tokenService;

    @MockitoBean
    protected LogoutService logoutService;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected OrderService orderService;
}
