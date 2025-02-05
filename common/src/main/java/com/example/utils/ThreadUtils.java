package com.example.utils;

public class ThreadUtils {
    private ThreadUtils() {
    }

    public static void startThread(ThrowingRunnable runnable) {
        Thread.startVirtualThread(runnable);
    }
}
