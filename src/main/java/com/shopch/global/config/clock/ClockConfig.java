package com.shopch.global.config.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class ClockConfig {

    public final static ZoneId TIME_ZONE = ZoneId.systemDefault();

    @Bean
    public java.time.Clock clock() {
        return java.time.Clock.system(TIME_ZONE);
    }

    @Bean
    public io.jsonwebtoken.Clock jwtClock() {
        return new JwtClock(clock());
    }
}
