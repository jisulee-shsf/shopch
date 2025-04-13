package com.app.global.config.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class TimeConfig {

    public final static ZoneId TIME_ZONE = ZoneId.of("Asia/Seoul");

    @Bean
    public java.time.Clock clock() {
        return java.time.Clock.system(TIME_ZONE);
    }

    @Bean
    public io.jsonwebtoken.Clock jwtClock() {
        return new JwtClock(clock());
    }
}
