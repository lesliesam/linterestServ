package com.linterest;

import com.linterest.utils.SecUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;

import java.util.Map;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class HibernateUtil extends DriverManagerConnectionProviderImpl {
    private static SessionFactory sessionFactory = buildSessionFactory();
    private static ServiceRegistry serviceRegistry;

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        serviceRegistry = new ServiceRegistryBuilder().applySettings(
                configuration.getProperties()). buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void configure(Map configurationValues) {
        String user = (String) configurationValues.get(Environment.USER);
        String password = (String) configurationValues.get(Environment.PASS);
        configurationValues.put(Environment.USER, SecUtil.decrypt(user));
        configurationValues.put(Environment.PASS, SecUtil.decrypt(password));
        super.configure(configurationValues);
    }
}
