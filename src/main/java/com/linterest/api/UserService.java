package com.linterest.api;

import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.linterest.HibernateUtil;
import com.linterest.dto.UserEntity;
import org.hibernate.Session;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@At("/user") @Service
public class UserService {

    @Get
    Reply<UserEntity> getUser() {
        UserEntity entity = new UserEntity();
        entity.setPassword("123456");
        entity.setPhoneNum(1234567890);
        entity.setUserName("ABC ttt");

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();
        session.close();

        return Reply.with(entity).as(Json.class);
    }
}
