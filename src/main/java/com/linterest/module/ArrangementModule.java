package com.linterest.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.GuiceInstance;
import com.linterest.entity.ArrangementEntity;
import com.linterest.entity.MenuEntity;
import com.linterest.entity.UserEntity;
import com.linterest.entity.UserHobbyEntity;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorParamInvalid;
import com.linterest.error.ServerErrorUserNotFound;
import com.linterest.services.ArrangementServices;
import com.linterest.services.MenuServices;
import com.linterest.services.UserServices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */

@Api(value = "Arrangement service")
@Path("/arrangement")
public class ArrangementModule {

    @POST
    @Path("/create")
    @ApiOperation(value = "设置创建饭局")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@FormParam("authSession") String authSession,
                           @FormParam("theme") String theme,
                           @FormParam("tag") String tag,
                           @FormParam("menuId") String menuId,
                           @FormParam("price") int price,
                           @FormParam("guestNum") int guestNum,
                           @FormParam("address") String address,
                           @FormParam("images") String images) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (theme == null || theme.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("theme"))).build();
        }

        if (tag == null || tag.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("tag"))).build();
        }

        if (menuId == null || menuId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("menuId"))).build();
        }

        if (address == null || address.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("address"))).build();
        }

        if (images == null || images.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("images"))).build();
        }

        if (guestNum <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("guest", "Must be > 0"))).build();
        }

        if (price < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("price", "Must be >= 0"))).build();
        }

        UserServices userServices = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = userServices.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        MenuServices menuServices = GuiceInstance.getGuiceInjector().getInstance(MenuServices.class);
        List<MenuEntity> menuList = menuServices.getById(menuId);
        if (menuList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("menuId", "1"))).build();
        }

        UserEntity user = list.get(0);
        MenuEntity menu = menuList.get(0);
        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        ArrangementEntity arrangement = arrangementServices.setup(user, theme, tag, price, guestNum, address, images, menu);

        return Response.status(Response.Status.OK).entity(gson.toJson(arrangement)).build();
    }

    @GET
    @Path("/getById/{id}")
    @ApiOperation(value = "获取可供选择的用餐列表", notes = "按兴趣匹配返回结果")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        Gson gson = new GsonBuilder().create();

        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> list = arrangementServices.getById(id);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("id", "1"))).build();
        }

        return Response.ok().entity(gson.toJson(list.get(0))).build();
    }

    @GET
    @Path("/getPendingHost")
    @ApiOperation(value = "获取可供选择的用餐列表", notes = "按兴趣匹配返回结果")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingHosts(@HeaderParam("authSession") String authSession) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        List<UserHobbyEntity> userHobbiesEntityList = services.getUserHobby(user);

        return Response.ok().build();
    }

    @POST
    @Path("/like")
    @ApiOperation(value = "喜欢此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId) {
        return updateUserLikeArrangement(authSession, arrangementId, false);
    }

    @POST
    @Path("/unlike")
    @ApiOperation(value = "喜欢此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId) {
        return updateUserLikeArrangement(authSession, arrangementId, true);
    }

    @GET
    @Path("/getLikedHost")
    @ApiOperation(value = "获取喜欢（收藏）的用餐列表")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikedHost(@HeaderParam("authSession") String authSession) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/join")
    @ApiOperation(value = "加入此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response join(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId,
                               @FormParam("isCoreHost") boolean isCoreHost) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (arrangementId == null || arrangementId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("arrangementId"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/quit")
    @ApiOperation(value = "退出此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response quit(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (arrangementId == null || arrangementId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("arrangementId"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        return Response.ok().build();
    }

    private Response updateUserLikeArrangement(String authSession, String arrangementId, boolean like) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (arrangementId == null || arrangementId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("arrangementId"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        return Response.ok().build();
    }
}
