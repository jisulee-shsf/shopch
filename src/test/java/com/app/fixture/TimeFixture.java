package com.app.fixture;

import java.time.Instant;

public class TimeFixture {

    public static final Instant FIXED_PAST_INSTANT = Instant.parse("2025-01-01T01:00:00Z");
    public static final long ACCESS_TOKEN_EXPIRATION_DURATION = 1000 * 60 * 15 * 15;
    public static final long REFRESH_TOKEN_EXPIRATION_DURATION = 1000 * 60 * 60 * 24 * 14;
}
