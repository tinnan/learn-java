package com.github.tinnan.jobrunner.constants;

import java.util.Arrays;

public enum Env {

    dev1,
    dev2,
    dev3,
    sit1,
    sit2,
    sit3,
    uat1,
    uat2;

    public static String[] names() {
        return Arrays.stream(Env.values()).map(Enum::name).toArray(String[]::new);
    }
}
