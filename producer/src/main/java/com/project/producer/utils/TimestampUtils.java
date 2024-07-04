package com.project.producer.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampUtils {

    public static String getTimestamp() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return now.format(formatter);
    }
}
