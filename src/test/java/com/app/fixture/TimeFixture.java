package com.app.fixture;

import java.time.Instant;

public class TimeFixture {

    public static final Instant FIXED_FUTURE_INSTANT = Instant.parse("2025-12-31T01:00:00Z");
    public static final Instant FIXED_PAST_INSTANT = Instant.parse("2025-01-01T01:00:00Z");
    public static final Long ACCESS_TOKEN_EXPIRATION_DURATION = 900000L;
    public static final Long REFRESH_TOKEN_EXPIRATION_DURATION = 1209600000L;
}
