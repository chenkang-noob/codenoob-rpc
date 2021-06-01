package com.imnoob.transport.netty.spi;

import java.util.ServiceLoader;

public class WarningFactory {

    private static final ServiceLoader<Warning> loader = ServiceLoader.load(Warning.class);

    public static Warning getWarning(Class clazz) {
        for (Warning item : loader) {
            if (item.getClass().equals(clazz)) return item;
        }
        return new Warning() {
            @Override
            public String sendWarning(String msg) {
                System.out.println("is origin Warning");
                return "origin";
            }
        };
    }

}
