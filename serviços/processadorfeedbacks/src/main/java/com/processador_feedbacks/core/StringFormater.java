package com.processador_feedbacks.core;


public class StringFormater {
    public static String removeSpaces(String value) {
        if (value == null) return "";
        return value.trim().replaceAll("\\s+", " ");
    }
}