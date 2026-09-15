package com.backline.bkln3195;

import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public final class Greeter {
    private Greeter() {}

    public static String greet(String name) {
        return StringSubstitutor.replace("Hello, ${name}!", Map.of("name", name));
    }
}
