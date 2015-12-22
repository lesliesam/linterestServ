package com.linterest;

import com.google.inject.Injector;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class GuiceInstance {
    private static Injector mInjector;

    public static void setGuiceInjector(Injector injector) {
        mInjector = injector;
    }

    public static Injector getGuiceInjector() {
        return mInjector;
    }
}
