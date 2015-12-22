package com.linterest.services;

import com.linterest.HibernateUtil;
import com.linterest.entity.PersonalityEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class PersonalityServicesImpl implements PersonalityServices {

    public static List<PersonalityEntity> gPersonalityCache = null;

    public List<PersonalityEntity> getAll() {
        if (gPersonalityCache == null) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            String queryStr = "from PersonalityEntity";
            gPersonalityCache = session.createQuery(queryStr).list();

            session.close();
        }

        return gPersonalityCache;
    }
}
