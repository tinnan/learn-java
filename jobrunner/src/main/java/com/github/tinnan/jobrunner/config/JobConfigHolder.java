package com.github.tinnan.jobrunner.config;

public class JobConfigHolder {

    private static final ThreadLocal<String> config = new ThreadLocal<>();

    private JobConfigHolder() {

    }

    public static void set(String value) {
        config.set(value);
    }

    public static String get() {
        return config.get();
    }

    public static void clear() {
        config.remove();
    }
}
