package com.gmail.fomichov.m.drillingmagazine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateUtils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); // задаем формат даты
    private static Calendar calendar = Calendar.getInstance(); // подключаем календарь

    private MyDateUtils() {
    }

    public static String getCurrentDate() {
        return dateFormat.format(calendar.getTime());
    }

    // сравниваем две даты
    public static Boolean datesComparing(Date dateStart, Date dateEnd) {
        return (dateStart.getTime() <= dateEnd.getTime()) ;
    }

    // конвентируем дату с текстового формата в обьект класса Date
    public static Date convertStringToDate(String date) throws ParseException {
        calendar.setTime(dateFormat.parse(date));
        return calendar.getTime();
    }

}
