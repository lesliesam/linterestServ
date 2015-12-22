package com.linterest.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.Constants;
import com.linterest.HibernateUtil;
import com.linterest.dto.UserHobbyDto;
import com.linterest.entity.HobbyEntity;
import com.linterest.entity.PersonalityEntity;
import com.linterest.entity.UserEntity;
import com.linterest.entity.UserHobbyEntity;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorParamInvalid;
import com.linterest.error.ServerErrorUserNotFound;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;

import javax.persistence.Cacheable;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Api(value = "User Profile service")
@Path("/userProfile")
public class UserProfileModule {

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

    @POST
    @Path("/setPersonality")
    @ApiOperation(value = "设置用户个性", notes = "除预设的个性，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPersonality(@FormParam("authSession") String authSession, @FormParam("personality") String personality){
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (personality == null || personality.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("gender"))).build();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        String queryStr = "from UserEntity where session = :session";
        List<UserEntity> list = session.createQuery(queryStr).
                setString("session", authSession).list();
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        // Get the hobby id from db.
        if (Constants.gPersonalityCache == null) {
            queryStr = "from PersonalityEntity";
            Constants.gPersonalityCache = session.createQuery(queryStr).list();
        }

        PersonalityEntity personalityEntity = personalityNameToEntity(personality);
        if (personalityEntity == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("personality",
                    gson.toJson(Constants.gPersonalityCache)))).build();
        }

        UserEntity user = list.get(0);
        user.setPersonality(personalityEntity.getId());
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }

    @GET
    @Path("/getHobby")
    @ApiOperation(value = "获取用户兴趣列表", notes = "获取所有兴趣列表，返回结果无序")
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

        // Get the hobby id from db.
        if (Constants.gHobbiesCache == null) {
            queryStr = "from HobbyEntity";
            Constants.gHobbiesCache = session.createQuery(queryStr).list();
        }

        UserEntity user = list.get(0);
        int id = user.getId();
        queryStr = "from UserHobbyEntity where userId = :userId and deleted = false";
        List<UserHobbyEntity> userHobbiesEntityList = session.createQuery(queryStr).
                setString("userId", String.valueOf(id)).list();

        return Response.ok().entity(gson.toJson(userHobbyEntityToDto(userHobbiesEntityList))).build();
    }

    @POST
    @Path("/addHobby")
    @ApiOperation(value = "添加用户兴趣爱好", notes = "除预设兴趣爱好，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHobby(@HeaderParam("authSession") String authSession, @FormParam("hobby") String hobby) {
        return updateUserHobby(authSession, hobby, false);
    }

    @POST
    @Path("/deleteHobby")
    @ApiOperation(value = "删除用户兴趣爱好", notes = "除预设兴趣爱好，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHobby(@HeaderParam("authSession") String authSession, @FormParam("hobby") String hobby) {
        return updateUserHobby(authSession, hobby, true);
    }

    private Response updateUserHobby(String authSession, String hobby, boolean deleted) {
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

        HobbyEntity hobbyEntity = hobbyNameToEntity(hobby);
        if (hobbyEntity == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("hobby",
                    gson.toJson(Constants.gHobbiesCache)))).build();
        }

        UserEntity user = list.get(0);
        int id = user.getId();
        queryStr = "from UserHobbyEntity where userId = :userId and hobbyId = :hobbyId";
        List<UserHobbyEntity> userHobbies = session.createQuery(queryStr).
                setString("userId", String.valueOf(id)).
                setString("hobbyId", String.valueOf(hobbyEntity.getId())).list();

        if (userHobbies.size() == 0) {
            UserHobbyEntity userHobbyEntity = new UserHobbyEntity();
            userHobbyEntity.setUserId(id);
            userHobbyEntity.setHobbyId(hobbyEntity.getId());
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

        return Response.status(Response.Status.OK).build();
    }


    private List<UserHobbyDto> userHobbyEntityToDto(List<UserHobbyEntity> userHobbyEntities) {
        List<UserHobbyDto> userHobbies = new ArrayList<UserHobbyDto>();
        Iterator<UserHobbyEntity> userHobbyIterator = userHobbyEntities.iterator();
        while (userHobbyIterator.hasNext()) {
            UserHobbyEntity userHobby = userHobbyIterator.next();
            HobbyEntity entity = hobbyIdToEntity(userHobby.getHobbyId());

            UserHobbyDto dto = new UserHobbyDto();
            dto.id = entity.getId();
            dto.hobbyName = entity.getHobbyName();
            userHobbies.add(dto);
        }

        return userHobbies;
    }

    private HobbyEntity hobbyNameToEntity(String hobby) {
        Iterator<HobbyEntity> it = Constants.gHobbiesCache.iterator();
        while (it.hasNext()) {
            HobbyEntity hobbyEntity = it.next();
            if (hobbyEntity.getHobbyName().equals(hobby)) {
                return hobbyEntity;
            }
        }
        return null;
    }

    private HobbyEntity hobbyIdToEntity(int id) {
        Iterator<HobbyEntity> it = Constants.gHobbiesCache.iterator();
        while (it.hasNext()) {
            HobbyEntity hobbyEntity = it.next();
            if (hobbyEntity.getId() == id) {
                return hobbyEntity;
            }
        }
        return null;
    }

    private PersonalityEntity personalityNameToEntity(String personality) {
        Iterator<PersonalityEntity> it = Constants.gPersonalityCache.iterator();
        while (it.hasNext()) {
            PersonalityEntity personalityEntity = it.next();
            if (personalityEntity.getName().equals(personality)) {
                return personalityEntity;
            }
        }
        return null;
    }
}
