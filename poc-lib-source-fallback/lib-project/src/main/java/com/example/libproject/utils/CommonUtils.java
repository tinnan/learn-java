package com.example.libproject.utils;

import org.apache.commons.lang3.StringUtils;

public class CommonUtils {

    public static String sayHelloTo(String ...name) {
        return String.format("Hello %s", StringUtils.join(name, ", "));
    }

    private CommonUtils() {}
}
