package com.example.utils;

public class LoggingUtils {
    private LoggingUtils() {
    }

    public static void log(String format, Object... args) {
        System.out.printf("[%s] %s", Thread.currentThread(), format.formatted(args));
    }
}
