package com.app.global.util;

import com.app.global.config.time.TimeConfig;

import java.time.LocalDateTime;
import java.util.Date;

public class DateTimeUtils {

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), TimeConfig.TIME_ZONE);
    }
}
