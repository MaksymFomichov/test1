package com.gmail.fomichov.m.drillingmagazine.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// нужен для проверки вводимой строки названия обьекта на символы,
// которые не позволят создать правильное имя файла, которое потом будет исользоваться при сохранении обьектов
public class CheckString {
    public static boolean isFileNameCorrect(String name) {
        Pattern pattern = Pattern.compile("(.+)?[><\\|\\?*/:\\\\\"](.+)?");
        Matcher matcher = pattern.matcher(name);
        return !matcher.find();
    }
}
