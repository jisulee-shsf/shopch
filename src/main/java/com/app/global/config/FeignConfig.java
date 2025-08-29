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

    public static final String APPLICATION_FORM_URLENCODED_UTF8_VALUE = MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8";

    @Value("${retry.period}")
    private long period;

    @Value("${retry.max-period}")
    private long maxPeriod;

    @Value("${retry.max-attempts}")
    private int maxAttempts;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header(HttpHeaders.CONTENT_TYPE, APPLICATION_FORM_URLENCODED_UTF8_VALUE);
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
