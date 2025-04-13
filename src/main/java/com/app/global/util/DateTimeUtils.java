package com.app.global.util;

import java.time.LocalDateTime;
import java.util.Date;

import static com.app.global.config.time.TimeConfig.TIME_ZONE;

public class DateTimeUtils {

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), TIME_ZONE);
    }
}
