package com.linterest.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.GuiceInstance;
import com.linterest.dto.UserHobbyDto;
import com.linterest.entity.HobbyEntity;
import com.linterest.entity.PersonalityEntity;
import com.linterest.entity.UserEntity;
import com.linterest.entity.UserHobbyEntity;
import com.linterest.error.ServerErrorAuthFailed;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorParamInvalid;
import com.linterest.error.ServerErrorUserNotFound;
import com.linterest.services.HobbyServices;
import com.linterest.services.PersonalityServices;
import com.linterest.services.UserServices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        UserEntity user = list.get(0);
        services.updateUserGender(user, gender);

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

        PersonalityServices pServices = GuiceInstance.getGuiceInjector().getInstance(PersonalityServices.class);
        List<PersonalityEntity> personalityEntities = pServices.getAll();

        PersonalityEntity personalityEntity = personalityNameToEntity(personality);
        if (personalityEntity == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("personality",
                    gson.toJson(personalityEntities)))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        UserEntity user = list.get(0);
        services.updateUserPersonality(user, personalityEntity);

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

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        UserEntity user = list.get(0);
        List<UserHobbyEntity> userHobbiesEntityList = services.getUserHobby(user);

        return Response.ok().entity(gson.toJson(userHobbyEntityToDto(userHobbiesEntityList))).build();
    }

    @POST
    @Path("/addHobby")
    @ApiOperation(value = "添加用户兴趣爱好", notes = "兴趣爱好以逗号分隔，除预设兴趣爱好，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHobby(@HeaderParam("authSession") String authSession, @FormParam("hobbies") String hobbies) {
        return updateUserHobby(authSession, hobbies, false);
    }

    @POST
    @Path("/deleteHobby")
    @ApiOperation(value = "删除用户兴趣爱好", notes = "兴趣爱好以逗号分隔，除预设兴趣爱好，其他字符串无效")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHobby(@HeaderParam("authSession") String authSession, @FormParam("hobbies") String hobbies) {
        return updateUserHobby(authSession, hobbies, true);
    }

    @POST
    @Path("/setDisplayName")
    @ApiOperation(value = "设置用户显示的名字")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDisplayName(@HeaderParam("authSession") String authSession, @FormParam("displayName") String displayName) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (displayName == null || displayName.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("displayName"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        UserEntity user = list.get(0);
        services.updateUserDisplayName(user, displayName);

        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }

    private Response updateUserHobby(String authSession, String hobbies, boolean deleted) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (hobbies == null || hobbies.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("hobby"))).build();
        }

        String[] hobbyArr = hobbies.split(",");
        HobbyServices hServices = GuiceInstance.getGuiceInjector().getInstance(HobbyServices.class);
        List<HobbyEntity> hobbyEntities = hServices.getAll();

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        // Check all before update.
        for (int i = 0; i < hobbyArr.length; i++) {
            String hobby = hobbyArr[i];

            HobbyEntity hobbyEntity = hobbyNameToEntity(hobby);
            if (hobbyEntity == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("hobby",
                        gson.toJson(hobbyEntities)))).build();
            }
        }

        // Update all.
        UserEntity user = list.get(0);
        for (int i = 0; i < hobbyArr.length; i++) {
            String hobby = hobbyArr[i];

            HobbyEntity hobbyEntity = hobbyNameToEntity(hobby);
            services.updateUserHobby(user, hobbyEntity, deleted);
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
        HobbyServices hServices = GuiceInstance.getGuiceInjector().getInstance(HobbyServices.class);
        List<HobbyEntity> hobbyEntities = hServices.getAll();
        Iterator<HobbyEntity> it = hobbyEntities.iterator();
        while (it.hasNext()) {
            HobbyEntity hobbyEntity = it.next();
            if (hobbyEntity.getHobbyName().equals(hobby)) {
                return hobbyEntity;
            }
        }
        return null;
    }

    private HobbyEntity hobbyIdToEntity(int id) {
        HobbyServices hServices = GuiceInstance.getGuiceInjector().getInstance(HobbyServices.class);
        List<HobbyEntity> hobbyEntities = hServices.getAll();
        Iterator<HobbyEntity> it = hobbyEntities.iterator();
        while (it.hasNext()) {
            HobbyEntity hobbyEntity = it.next();
            if (hobbyEntity.getId() == id) {
                return hobbyEntity;
            }
        }
        return null;
    }

    private PersonalityEntity personalityNameToEntity(String personality) {
        PersonalityServices pServices = GuiceInstance.getGuiceInjector().getInstance(PersonalityServices.class);
        List<PersonalityEntity> personalityEntities = pServices.getAll();
        Iterator<PersonalityEntity> it = personalityEntities.iterator();
        while (it.hasNext()) {
            PersonalityEntity personalityEntity = it.next();
            if (personalityEntity.getName().equals(personality)) {
                return personalityEntity;
            }
        }
        return null;
    }
}
