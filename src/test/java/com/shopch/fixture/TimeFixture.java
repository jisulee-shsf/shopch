package com.shopch.fixture;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeFixture {

    public static final Instant INSTANT_NOW = Instant.parse("2025-01-01T01:00:00Z");
    public static final ZoneId TEST_TIME_ZONE = ZoneOffset.UTC;
    public static final int ONE_SECOND_IN_MILLIS = 1000;
}
