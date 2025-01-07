package com.yourcompany.rentalmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeFormat {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String dateToString(LocalDate date) {
        return formatter.format(date);
    }

    public static LocalDate stringToDate(String date) {
        return LocalDate.parse(date, formatter);
    }
}
