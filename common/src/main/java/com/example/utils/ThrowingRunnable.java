package com.example.utils;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {
    @Override
    default void run() {
        try {
            tryRun();
        } catch (Throwable t) {
            throwUnchecked(t);
        }
    }

    private static <E extends Throwable> void throwUnchecked(Throwable t) throws E {
        throw (E) t;
    }

    void tryRun() throws Throwable;
}
