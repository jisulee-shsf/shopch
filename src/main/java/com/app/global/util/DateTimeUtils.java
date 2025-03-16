package com.app.global.util;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

public class DateTimeUtils {

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), systemDefault());
    }
}
