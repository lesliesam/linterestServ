package com.linterest.services;

import com.linterest.HibernateUtil;
import com.linterest.entity.MenuEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class MenuServicesImpl implements MenuServices {

    @Override
    public List<MenuEntity> getById(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from MenuEntity where id = :id";
        List<MenuEntity> list = session.createQuery(queryStr).
                setString("id", id).
                list();

        session.close();
        return list;
    }
}
