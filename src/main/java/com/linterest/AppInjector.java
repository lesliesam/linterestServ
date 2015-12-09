package com.linterest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.sitebricks.SitebricksModule;
import com.linterest.api.DefaultPage;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class AppInjector extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        HibernateUtil.getSessionFactory();

        return Guice.createInjector(new SitebricksModule(){
            @Override
            protected void configureSitebricks() {
                scan(DefaultPage.class.getPackage());
            }
        });
    }
}
