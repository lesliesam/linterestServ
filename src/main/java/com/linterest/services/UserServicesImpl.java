package com.linterest.services;

import com.linterest.Constants;
import com.linterest.HibernateUtil;
import com.linterest.annotation.CacheEnabled;
import com.linterest.annotation.UserSession;
import com.linterest.entity.*;
import com.linterest.interceptor.UserSessionCacheInterceptor;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public class UserServicesImpl implements UserServices {

    public List<UserEntity> getUser(String userName) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from UserEntity where userName = :userName";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("userName", userName).
                list();

        session.close();
        return list;
    }

    @UserSession(type = UserSessionCacheInterceptor.GET)
    @CacheEnabled
    public List<UserEntity> getUserWithAuthSession(String authSession) {
        return new ArrayList<UserEntity>();
    }

    public List<UserHobbyEntity> getUserHobby(UserEntity user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String queryStr = "from UserHobbyEntity where userId = :userId and deleted = false";
        List<UserHobbyEntity> list = session.createQuery(queryStr).
                setString("userId", String.valueOf(user.getId())).list();

        session.close();
        return list;
    }

    public UserEntity userSignup(String userName, String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        UserEntity user = new UserEntity();
        user.setUserName(userName);
        user.setPassword(password);

        session.getTransaction().begin();
        session.persist(user);
        session.getTransaction().commit();

        session.close();
        return user;
    }

    public UserEntity userSignupWithDevice(String deviceName, String deviceId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from UserDeviceIdEntity where deviceName = :deviceName and deviceId = :deviceId";
        List<UserDeviceIdEntity> list = session.createQuery(queryStr).
                setString("deviceName", deviceName).
                setString("deviceId", deviceId)
                .list();
        UserEntity user;
        if (list.size() > 0) {
            // Device found.
            UserDeviceIdEntity deviceIdEntity = list.get(0);
            user = deviceIdEntity.getUser();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } else {
            // Device not found.
            user = new UserEntity();
            user.setUserName(Constants.GUEST_NAME);
            user.setPassword(Constants.GUEST_PASSWORD);

            UserDeviceIdEntity deviceIdEntity = new UserDeviceIdEntity();
            deviceIdEntity.setDeviceName(deviceName);
            deviceIdEntity.setDeviceId(deviceId);
            deviceIdEntity.setUser(user);

            session.beginTransaction();
            session.save(deviceIdEntity);
            session.getTransaction().commit();

            user = deviceIdEntity.getUser();
        }

        session.close();
        return user;
    }

    @UserSession(type = UserSessionCacheInterceptor.STORE)
    @CacheEnabled
    public UserEntity updateUserSession(UserEntity user) {
        user.setSession(generateUserSessionStr(user.getUserName(), user.getPassword()));

        return user;
    }

    public UserEntity updateUserGender(UserEntity user, String gender) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        user.setGender(gender);
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        session.close();
        return user;
    }

    public UserEntity updateUserPersonality(UserEntity user, PersonalityEntity personality) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        user.setPersonality(personality.getId());
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        session.close();
        return user;
    }

    public UserEntity updateUserHobby(UserEntity user, HobbyEntity hobby, boolean deleted) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        int id = user.getId();

        String queryStr = "from UserHobbyEntity where userId = :userId and hobbyId = :hobbyId";
        List<UserHobbyEntity> userHobbies = session.createQuery(queryStr).
                setString("userId", String.valueOf(id)).
                setString("hobbyId", String.valueOf(hobby.getId())).list();

        if (userHobbies.size() == 0) {
            UserHobbyEntity userHobbyEntity = new UserHobbyEntity();
            userHobbyEntity.setUserId(id);
            userHobbyEntity.setHobbyId(hobby.getId());
            userHobbyEntity.setDeleted(deleted);

            session.beginTransaction();
            session.save(userHobbyEntity);
            session.getTransaction().commit();
        } else {
            UserHobbyEntity userHobbyEntity = userHobbies.get(0);
            userHobbyEntity.setDeleted(deleted);
            session.beginTransaction();
            session.update(userHobbyEntity);
            session.getTransaction().commit();
        }

        session.close();
        return user;
    }

    @Override
    public UserEntity updateUserDisplayName(UserEntity user, String displayName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        user.setDisplayName(displayName);
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        session.close();
        return user;
    }

    @Override
    public UserEntity updatePhoneNumber(UserEntity user, String phoneNumber) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        user.setPhoneNum(phoneNumber);
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        session.close();
        return user;
    }

    private String generateUserSessionStr(String userName, String password) {
        String sessionBeforeCrypt = userName + password + System.currentTimeMillis();
        return DigestUtils.md5Hex(sessionBeforeCrypt);
    }
}
