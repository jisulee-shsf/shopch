package com.app.global.config.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    public java.time.Clock clock() {
        return java.time.Clock.systemDefaultZone();
    }

    @Bean
    public io.jsonwebtoken.Clock jwtClock() {
        return new JwtClock(clock());
    }
}
