package com.shopch.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopch.api.auth.controller.AuthController;
import com.shopch.api.auth.service.AuthService;
import com.shopch.api.health.controller.HealthCheckController;
import com.shopch.api.member.controller.MemberController;
import com.shopch.api.member.service.MemberAccountService;
import com.shopch.api.order.controller.OrderController;
import com.shopch.api.order.service.OrderService;
import com.shopch.api.product.controller.ProductController;
import com.shopch.api.product.service.ProductService;
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
                AuthController.class,
                MemberController.class,
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
    protected AuthService authService;

    @MockitoBean
    protected MemberAccountService memberAccountService;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected OrderService orderService;
}
