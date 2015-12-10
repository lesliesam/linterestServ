package com.linterest.api;

import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;
import com.linterest.HibernateUtil;
import com.linterest.dto.UserEntity;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@At("/user") @Service
public class UserService {

    private static final String QUERY_USER = "from UserEntity";

    @At("/all")
    @Get
    public Reply<?> getAllUser(Request request) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery(QUERY_USER);
        List<UserEntity> list = query.list();
        session.close();

        return Reply.with(list).as(Json.class);
    }

    @At("/:name")
    @Get
    public Reply<?> getUser(Request request, @Named("name") String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery(QUERY_USER + " where userName = '" + name + "'");
        List<UserEntity> list = query.list();
        session.close();

        return Reply.with(list).as(Json.class);
    }

    @Post
    public Reply<?> addUser(Request request) {
        try {
            UserEntity user = request.read(UserEntity.class).as(Json.class);

            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            return Reply.saying().error();
        }

        return Reply.saying().ok();
    }
}
