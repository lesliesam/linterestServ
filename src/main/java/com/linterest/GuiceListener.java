package com.linterest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.linterest.annotation.CacheEnabled;
import com.linterest.interceptor.CacheInterceptor;
import com.linterest.services.*;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class GuiceListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(new ServletModule() {
            // Configure your IOC
            @Override
            protected void configureServlets() {
                bind(UserServices.class).to(UserServicesImpl.class);
                bind(PersonalityServices.class).to(PersonalityServicesImpl.class);
                bind(HobbyServices.class).to(HobbyServicesImpl.class);

                bindInterceptor(Matchers.any(),
                        Matchers.annotatedWith(CacheEnabled.class),
                        new CacheInterceptor());
            }
        });
        GuiceInstance.setGuiceInjector(injector);
        return injector;
    }
}
