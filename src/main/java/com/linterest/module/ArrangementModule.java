package com.linterest.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.GuiceInstance;
import com.linterest.entity.*;
import com.linterest.error.ServerErrorAuthFailed;
import com.linterest.error.ServerErrorParamEmpty;
import com.linterest.error.ServerErrorParamInvalid;
import com.linterest.error.ServerErrorWithString;
import com.linterest.services.ArrangementServices;
import com.linterest.services.MenuServices;
import com.linterest.services.UserServices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */

@Api(value = "Arrangement service")
@Path("/arrangement")
public class ArrangementModule {

    @POST
    @Path("/create")
    @ApiOperation(value = "设置创建饭局", notes = "facilities用逗号分割，可以是TV,wifi,carParking,Photograph")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@FormParam("authSession") String authSession,
                           @FormParam("theme") String theme,
                           @FormParam("tag") String tag,
                           @FormParam("menuId") String menuId,
                           @FormParam("price") int price,
                           @FormParam("guestNum") int guestNum,
                           @FormParam("address") String address,
                           @FormParam("latitude") float latitude,
                           @FormParam("longitude") float longitude,
                           @FormParam("facilities") String facilities,
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
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        MenuServices menuServices = GuiceInstance.getGuiceInjector().getInstance(MenuServices.class);
        List<MenuEntity> menuList = menuServices.getById(menuId);
        if (menuList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("menuId", "1"))).build();
        }

        UserEntity user = list.get(0);
        MenuEntity menu = menuList.get(0);
        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        ArrangementEntity arrangement = arrangementServices.setup(user, theme, tag, price, guestNum, address, latitude, longitude, images, facilities, menu);

        return Response.status(Response.Status.OK).entity(gson.toJson(arrangement)).build();
    }

    @GET
    @Path("/getById/{id}")
    @ApiOperation(value = "获取某次用餐信息", notes = "id是用餐的数据库索引值")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        Gson gson = new GsonBuilder().create();

        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> list = arrangementServices.getById(id);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("id", "2"))).build();
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
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        UserEntity user = list.get(0);
        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> arrangementList = arrangementServices.getNewByUser(user, 0);

        // 删除自己创建的
        for (int i = arrangementList.size() - 1; i >= 0 ; i--) {
            ArrangementEntity arrangement = arrangementList.get(i);
            if (arrangement.getHost().equals(user)) {
                arrangementList.remove(i);
            }
        }

        return Response.ok().entity(gson.toJson(arrangementList)).build();
    }

    @POST
    @Path("/like")
    @ApiOperation(value = "喜欢此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId) {
        return updateUserLikeArrangement(authSession, arrangementId, true);
    }

    @POST
    @Path("/unlike")
    @ApiOperation(value = "喜欢此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId) {
        return updateUserLikeArrangement(authSession, arrangementId, false);
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
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        UserEntity user = list.get(0);
        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> arrangementList = arrangementServices.getLikedByUser(user);

        return Response.ok().entity(gson.toJson(arrangementList)).build();
    }

    @POST
    @Path("/join")
    @ApiOperation(value = "加入此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response join(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId,
                               @FormParam("guestNum") int guestNum, @FormParam("isCoHost") boolean isCoHost) {
        return joinOrQuitArrangement(authSession, arrangementId, guestNum, isCoHost, true);
    }

    @POST
    @Path("/quit")
    @ApiOperation(value = "退出此用餐")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response quit(@HeaderParam("authSession") String authSession, @FormParam("arrangementId") String arrangementId) {
        return joinOrQuitArrangement(authSession, arrangementId, 0, false, false);
    }

    @GET
    @Path("/getArrangementGuest/{arrangementId}")
    @ApiOperation(value = "获取某次用餐的客人列表")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArrangementGuest(@HeaderParam("authSession") String authSession, @PathParam("arrangementId") String arrangementId) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> arrangementList = arrangementServices.getById(arrangementId);
        if (arrangementList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("arrangementId","2"))).build();
        }

        List<ArrangementGuestEntity> guestEntityList = arrangementServices.getAllGuestInArrangement(arrangementList.get(0));

        return Response.ok().entity(gson.toJson(guestEntityList)).build();
    }

    private Response updateUserLikeArrangement(String authSession, String arrangementId, boolean like) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        if (arrangementId == null || arrangementId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("arrangementId"))).build();
        }

        UserServices userServices = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> userList = userServices.getUserWithAuthSession(authSession);
        if (userList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> arrangementList = arrangementServices.getById(arrangementId);
        if (arrangementList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("arrangementId","2"))).build();
        }

        UserEntity user = userList.get(0);
        ArrangementEntity arrangement = arrangementList.get(0);

        if (arrangement.getHost().equals(user)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorWithString("Cannot like yourselves arrangement."))).build();
        }

        UserArrangementLikeEntity likeEntity = arrangementServices.userLikeArrangement(userList.get(0), arrangement, like);

        return Response.ok().entity(gson.toJson(likeEntity)).build();
    }

    private Response joinOrQuitArrangement(String authSession, String arrangementId, int guestNum, boolean isCoHost, boolean isJoin) {
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
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        ArrangementServices arrangementServices = GuiceInstance.getGuiceInjector().getInstance(ArrangementServices.class);
        List<ArrangementEntity> arrangementList = arrangementServices.getById(arrangementId);
        if (arrangementList.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("arrangementId","2"))).build();
        }

        UserEntity user = list.get(0);
        ArrangementEntity arrangement = arrangementList.get(0);

        if (arrangement.getHost().equals(user)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorWithString("Cannot join yourselves arrangement."))).build();
        }

        if (isJoin) {
            List<ArrangementGuestEntity> guestEntityList = arrangementServices.getAllGuestInArrangement(arrangement);

            int currentGuestNum = 0;
            boolean isThereACoHostExist = false;
            Iterator<ArrangementGuestEntity> iterator = guestEntityList.iterator();
            while (iterator.hasNext()) {
                ArrangementGuestEntity guestEntity = iterator.next();
                currentGuestNum += guestEntity.getGuestNum();
                if (guestEntity.getIsCoreHost()) {
                    isThereACoHostExist = true;
                }
            }

            if (currentGuestNum + guestNum > arrangement.getGuestNum()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorWithString("Arrangement does not has enough seat available."))).build();
            }

            if (isCoHost && isThereACoHostExist) {
                return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorWithString("Arrangement already has a cohost."))).build();
            }
        }

        ArrangementGuestEntity guestEntity = arrangementServices.joinOrQuitArrangement(user, arrangement, guestNum, isCoHost, isJoin);

        return Response.ok().entity(gson.toJson(guestEntity)).build();
    }
}
