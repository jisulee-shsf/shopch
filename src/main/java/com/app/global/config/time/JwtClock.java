package com.app.global.config.time;

import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class JwtClock implements io.jsonwebtoken.Clock {

    private final java.time.Clock clock;

    @Override
    public Date now() {
        return Date.from(clock.instant());
    }
}
