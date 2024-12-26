package com.yourcompany.rentalmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TimeFormat {

    public static String dateFormat(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return formatter.format(date);
    }
}
