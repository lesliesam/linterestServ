package com.linterest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.linterest.annotation.CacheEnabled;
import com.linterest.annotation.UserSession;
import com.linterest.interceptor.UserSessionCacheInterceptor;
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
                bind(ArrangementServices.class).to(ArrangementServicesImpl.class);
                bind(MenuServices.class).to(MenuServicesImpl.class);
                bind(CacheClientImpl.class).in(Singleton.class);

                bindInterceptor(Matchers.any(),
                        Matchers.annotatedWith(CacheEnabled.class).and(Matchers.annotatedWith(UserSession.class)),
                        new UserSessionCacheInterceptor());
            }
        });
        GuiceInstance.setGuiceInjector(injector);
        return injector;
    }
}
