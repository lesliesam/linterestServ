package com.linterest.services;

import com.linterest.HibernateUtil;
import com.linterest.entity.ArrangementEntity;
import com.linterest.entity.MenuEntity;
import com.linterest.entity.UserEntity;
import org.hibernate.Session;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ArrangementServicesImpl implements ArrangementServices {

    @Override
    public ArrangementEntity setup(UserEntity host, String theme, String tag, float price, int guestNum, String address, String images, MenuEntity menu) {
        ArrangementEntity arrangement = new ArrangementEntity();
        arrangement.setHost(host);
        arrangement.setTheme(theme);
        arrangement.setTag(tag);
        arrangement.setPrice(price);
        arrangement.setGuestNum(guestNum);
        arrangement.setLocation(address);
        arrangement.setImages(images);
        arrangement.setMenu(menu);

        Session session = HibernateUtil.getSessionFactory().openSession();

        session.getTransaction().begin();
        session.saveOrUpdate(arrangement);
        session.getTransaction().commit();

        session.close();

        return arrangement;
    }

    @Override
    public List<ArrangementEntity> getById(String id) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from ArrangementEntity where id = :id";
        List<ArrangementEntity> list = session.createQuery(queryStr).
                setString("id", id).
                list();

        session.close();
        return list;
    }
}
