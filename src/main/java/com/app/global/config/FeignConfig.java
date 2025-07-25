package com.app.global.config;

import com.app.global.error.FeignExceptionErrorDecoder;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@EnableFeignClients(basePackages = "com.app")
@Import(FeignClientsConfiguration.class)
public class FeignConfig {

    public static final String APPLICATION_FORM_URLENCODED_UTF8 = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8";

    @Value("${retry.period}")
    private Long period;

    @Value("${retry.max-period}")
    private Long maxPeriod;

    @Value("${retry.max-attempts}")
    private Integer maxAttempts;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_UTF8);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignExceptionErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(period, maxPeriod, maxAttempts);
    }
}
