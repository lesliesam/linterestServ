package com.linterest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.Constants;
import com.linterest.HibernateUtil;
import com.linterest.dto.HobbyEntity;
import com.linterest.dto.UserEntity;
import com.linterest.dto.UserHobbyEntity;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorParamInvalid;
import com.linterest.error.ServerErrorUserNotFound;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Api(value = "User Profile service")
@Path("/userProfile")
public class UserProfileService {
    @POST
    @Path("/setGender")
    @ApiOperation(value = "设置用户性别", notes = "性别可以是male或者female，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setGender(@FormParam("authSession") String authSession, @FormParam("gender") String gender){
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (gender == null || gender.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("gender"))).build();
        }

        if (!gender.equals("male") && !gender.equals("female")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("gender",
                    "male or female"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        String queryStr = "from UserEntity where session = :session";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("session", authSession).list();
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        user.setGender(gender);
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }

    @GET
    @Path("/getHobby")
    @ApiOperation(value = "获取用户兴趣列表", notes = "")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHobby(@HeaderParam("authSession") String authSession) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        String queryStr = "from UserEntity where session = :session";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("session", authSession).list();
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        int id = user.getId();
        queryStr = "from UserHobbyEntity where userId = :userId";
        List<UserHobbyEntity> userHobbies = session.createQuery(queryStr).
                setString("userId", String.valueOf(id)).list();

        return Response.ok().entity(gson.toJson(userHobbies)).build();
    }

    @POST
    @Path("/setHobby")
    @ApiOperation(value = "设置用户兴趣爱好", notes = "有多个预设的兴趣爱好，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setHobby(@FormParam("authSession") String authSession, @FormParam("hobby") String hobby) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (hobby == null || hobby.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("hobby"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        String queryStr = "from UserEntity where session = :session";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("session", authSession).list();
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        // Get the hobby id from db.
        if (Constants.gHobbiesCache == null) {
            queryStr = "from HobbyEntity";
            Constants.gHobbiesCache = session.createQuery(queryStr).list();
        }

        Iterator<HobbyEntity> it = Constants.gHobbiesCache.iterator();
        int hobbyId = -1;
        while (it.hasNext()) {
            HobbyEntity hobbyEntity = it.next();
            if (hobbyEntity.getHobbyName().equals(hobby)) {
                hobbyId = hobbyEntity.getId();
                break;
            }
        }

        if (hobbyId == -1) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("personality",
                    gson.toJson(Constants.gHobbiesCache)))).build();
        }

        UserEntity user = list.get(0);
        int id = user.getId();
        queryStr = "from UserHobbyEntity where userId = :userId";
        List<UserHobbyEntity> userHobbies = session.createQuery(queryStr).
                setString("userId", String.valueOf(id)).list();
        Iterator<UserHobbyEntity> hobbyIt = userHobbies.iterator();
        boolean alreadyExist = false;
        while (hobbyIt.hasNext()) {
            UserHobbyEntity userHobbyEntity = hobbyIt.next();
            if (userHobbyEntity.getHobbyId() == hobbyId) {
                alreadyExist = true;
                break;
            }
        }

        if (!alreadyExist) {
            UserHobbyEntity userHobbyEntity = new UserHobbyEntity();
            userHobbyEntity.setUserId(id);
            userHobbyEntity.setHobbyId(hobbyId);

            session.beginTransaction();
            session.save(userHobbyEntity);
            session.getTransaction().commit();
        }

        return Response.status(Response.Status.OK).build();
    }
}
