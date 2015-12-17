package com.linterest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.HibernateUtil;
import com.linterest.dto.UserDeviceIdEntity;
import com.linterest.dto.UserEntity;
import com.linterest.error.*;
import io.swagger.annotations.Api;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Api(value = "User service")
@Path("/user")
public class UserService {

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUser() {
        Gson gson = new GsonBuilder().create();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryUser = "from UserEntity";
        Query query = session.createQuery(queryUser);
        query.setMaxResults(10);
        List<UserEntity> list = query.list();
        session.close();

        return gson.toJson(list);
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signup(@FormParam("userName") String userName, @FormParam("password") String password) {
        Gson gson = new GsonBuilder().create();

        if (userName == null || userName.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("userName"))).build();
        }

        if (password == null || password.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("password"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from UserEntity where userName = :userName";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("userName", userName).
                list();
        if (list.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorDuplicateUsername(userName))).build();
        }

        UserEntity user = new UserEntity();
        user.setUserName(userName);
        user.setPassword(password);
        session.getTransaction().begin();
        session.persist(user);
        session.getTransaction().commit();
        session.close();

        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }

    @POST
    @Path("/signupWithDeviceId")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signupWithDeviceId(@FormParam("deviceName") String deviceName, @FormParam("deviceId") String deviceId) {
        Gson gson = new GsonBuilder().create();

        if (deviceName == null || deviceName.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("deviceId"))).build();
        }

        if (deviceId == null || deviceId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("deviceId"))).build();
        }

        if (!deviceName.equals("ios") && !deviceName.equals("android")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("deviceName",
                    "ios or android"))).build();
        }

        if (deviceId.length() != 32) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("deviceId",
                    "a1b27dc3c577446a2bcbd77935bda1b2"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();

        String queryStr = "from UserDeviceIdEntity where deviceName = :deviceName and deviceId = :deviceId";
        List<UserDeviceIdEntity> list = session.createQuery(queryStr).
                setString("deviceName", deviceName).
                setString("deviceId", deviceId)
                .list();
        if (list.size() > 0) {
            // Device found.`
            UserDeviceIdEntity deviceIdEntity = list.get(0);
            session.close();

            return Response.status(Response.Status.OK).entity(gson.toJson(deviceIdEntity.getUser())).build();
        } else {
            UserEntity user = new UserEntity();
            user.setUserName("Guest");
            user.setPassword("1234567");
            UserDeviceIdEntity deviceIdEntity = new UserDeviceIdEntity();
            deviceIdEntity.setDeviceName(deviceName);
            deviceIdEntity.setDeviceId(deviceId);

            session.beginTransaction();
            session.save(user);
            deviceIdEntity.setUser(user);
            session.save(deviceIdEntity);
            session.getTransaction().commit();
            session.close();

            return Response.status(Response.Status.OK).entity(gson.toJson(deviceIdEntity.getUser())).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("userName") String userName, @FormParam("password") String password ) {
        Gson gson = new GsonBuilder().create();

        if (userName == null || userName.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("userName"))).build();
        }

        if (password == null || password.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("password"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        String queryStr = "from UserEntity where userName = :userName";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("userName", userName).list();
        session.close();

        if (list.size() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        if (user.getPassword().equals(password)) {
            return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorPasswordMismatch())).build();
        }
    }
}
