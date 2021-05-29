package com.gradproject.hospi.utils;

import java.time.format.DateTimeFormatter;

public class DateTimeFormat {
    public static DateTimeFormatter date(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public static DateTimeFormatter time(){
        return DateTimeFormatter.ofPattern("HH:mm");
    }
}
