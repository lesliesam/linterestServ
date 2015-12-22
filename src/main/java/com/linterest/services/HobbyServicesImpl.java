package com.linterest.services;

import com.linterest.HibernateUtil;
import com.linterest.entity.HobbyEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class HobbyServicesImpl implements HobbyServices {

    public static List<HobbyEntity> gHobbiesCache = null;

    public List<HobbyEntity> getAll() {
        if (gHobbiesCache == null) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            String queryStr = "from HobbyEntity";
            gHobbiesCache = session.createQuery(queryStr).list();

            session.close();
        }

        return gHobbiesCache;
    }
}
