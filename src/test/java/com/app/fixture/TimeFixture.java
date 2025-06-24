package com.app.fixture;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeFixture {

    public static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T01:00:00Z");
    public static final ZoneId FIXED_TIME_ZONE = ZoneOffset.UTC;
}
