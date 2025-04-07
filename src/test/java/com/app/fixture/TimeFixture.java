package com.app.fixture;

import java.time.Clock;
import java.time.Instant;

import static java.time.ZoneId.systemDefault;

public class TimeFixture {

    public static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T01:00:00Z");
    public static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, systemDefault());
}
