package com.linterest.services;

import com.linterest.HibernateUtil;
import com.linterest.entity.ArrangementEntity;
import com.linterest.entity.MenuEntity;
import com.linterest.entity.UserArrangementLikeEntity;
import com.linterest.entity.UserEntity;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class ArrangementServicesImpl implements ArrangementServices {

    public static final int  MAX_RESULT_NUM = 20;

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

    @Override
    public List<ArrangementEntity> getNewByUser(UserEntity user, int limit) {
        if (limit == 0) {
            limit = MAX_RESULT_NUM;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from ArrangementEntity";
        List<ArrangementEntity> list = session.createQuery(queryStr).
                setMaxResults(limit).
                list();

        // Remove those arrangements that has been marked.
        queryStr = "from UserArrangementLikeEntity where user = :user";
        List<UserArrangementLikeEntity> likeEntityList = session.createQuery(queryStr).
                setEntity("user", user).
                list();

        Iterator<UserArrangementLikeEntity> iterator = likeEntityList.iterator();
        while (iterator.hasNext()) {
            UserArrangementLikeEntity likeEntity = iterator.next();
            list.remove(likeEntity.getArrangement());
        }

        session.close();
        return list;
    }

    @Override
    public List<ArrangementEntity> getLikedByUser(UserEntity user) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from UserArrangementLikeEntity where user = :user and isLike = :islike";
        List<UserArrangementLikeEntity> likeEntityList = session.createQuery(queryStr).
                setEntity("user", user).
                setBoolean("islike", true).
                list();

        List<ArrangementEntity> list = new ArrayList<>();
        Iterator<UserArrangementLikeEntity> iterator = likeEntityList.iterator();
        while (iterator.hasNext()) {
            UserArrangementLikeEntity likeEntity = iterator.next();
            list.add(likeEntity.getArrangement());
        }

        session.close();
        return list;
    }

    @Override
    public UserArrangementLikeEntity userLikeArrangement(UserEntity user, ArrangementEntity arrangementEntity, boolean like) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from UserArrangementLikeEntity where user = :user and arrangement = :arrangement";
        List<UserArrangementLikeEntity> likeEntityList = session.createQuery(queryStr).
                setEntity("user", user).
                setEntity("arrangement", arrangementEntity).
                list();

        UserArrangementLikeEntity likeEntity;
        if (likeEntityList.size() > 0) {
            likeEntity = likeEntityList.get(0);
            likeEntity.setIsLike(like);
            likeEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));

            session.beginTransaction();
            session.update(likeEntity);
            session.getTransaction().commit();
        } else {
            likeEntity = new UserArrangementLikeEntity();
            likeEntity.setUser(user);
            likeEntity.setArrangement(arrangementEntity);
            likeEntity.setIsLike(like);
            likeEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));

            session.getTransaction().begin();
            session.saveOrUpdate(likeEntity);
            session.getTransaction().commit();
        }

        session.close();

        return likeEntity;
    }
}
