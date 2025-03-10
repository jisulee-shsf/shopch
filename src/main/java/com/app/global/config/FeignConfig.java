package com.app.global.config;

import com.app.global.error.FeignClientExceptionErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFeignClients(basePackages = "com.app")
@Import(FeignClientsConfiguration.class)
public class FeignConfig {

    @Value("${retry.period}")
    private Long period;

    @Value("${retry.max-period}")
    private Long maxPeriod;

    @Value("${retry.max-attempts}")
    private Integer maxAttempts;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientExceptionErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(period, maxPeriod, maxAttempts);
    }
}
