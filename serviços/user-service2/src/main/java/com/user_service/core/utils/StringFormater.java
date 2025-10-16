package com.user_service.core.utils;

public class StringFormater {
    private StringFormater() {
    }
    public static String normalizeSpaces(String valor) {
        if (valor == null) return "";
        return valor.trim().replaceAll("\\s+", " ");
    }
}