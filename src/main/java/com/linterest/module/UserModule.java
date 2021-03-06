package com.linterest.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linterest.GuiceInstance;
import com.linterest.dto.UserPublicInfo;
import com.linterest.entity.*;
import com.linterest.error.*;
import com.linterest.services.UserServices;
import io.swagger.annotations.*;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
@Api(value = "User service")
@Path("/user")
public class UserModule {

    @POST
    @Path("/signup")
    @ApiOperation(value = "用户使用用户名和密码注册", notes = "服务器校验：用户名重名检测，密码校验等")
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

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUser(userName);
        if (list.size() > 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorDuplicateUsername(userName))).build();
        }

        UserEntity user = services.userSignup(userName, password);
        services.updateUserSession(user);
        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }

    @POST
    @Path("/signupWithDeviceId")
    @ApiOperation(value = "用户使用设备ID号注册和登陆", notes = "注册和登陆都调用此API，若设备号已经存在则返回现有用户，不存在则新建用户。")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signupWithDeviceId(@FormParam("deviceName") String deviceName, @FormParam("deviceId") String deviceId) {
        Gson gson = new GsonBuilder().create();

        if (deviceName == null || deviceName.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("deviceName"))).build();
        }

        if (deviceId == null || deviceId.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamEmpty("deviceId"))).build();
        }

        if (!deviceName.equals("ios") && !deviceName.equals("android")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorParamInvalid("deviceName",
                    "ios or android"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        UserEntity user = services.userSignupWithDevice(deviceName, deviceId);
        services.updateUserSession(user);

        return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
    }

    @POST
    @Path("/login")
    @ApiOperation(value = "用户使用用户名和密码登录", notes = "用户名和密码校验")
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

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUser(userName);
        if (list.size() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        if (user.getPassword().equals(DigestUtils.md5Hex(password))) {
            services.updateUserSession(user);
            return Response.status(Response.Status.OK).entity(gson.toJson(user)).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorPasswordMismatch())).build();
        }
    }

    @GET
    @Path("/getUserPublicInfo/{userId}")
    @ApiOperation(value = "获取用户公开信息")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPublicInfo(@HeaderParam("authSession") String authSession, @PathParam("userId") int userId) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        list = services.getUser(userId);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        UserPublicInfo userPublicInfo = new UserPublicInfo(user);

        return Response.ok().entity(gson.toJson(userPublicInfo)).build();
    }

    @GET
    @Path("/getComments/{userId}")
    @ApiOperation(value = "获取用户用餐反馈", notes = "按星数优先排序")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@HeaderParam("authSession") String authSession, @PathParam("userId") int userId) {
        Gson gson = new GsonBuilder().create();

        if (authSession == null || authSession.length() == 0) {
            return Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(new ServerErrorParamEmpty("authSession"))).build();
        }

        UserServices services = GuiceInstance.getGuiceInjector().getInstance(UserServices.class);
        List<UserEntity> list = services.getUserWithAuthSession(authSession);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorAuthFailed())).build();
        }

        list = services.getUser(userId);
        if (list.size() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(new ServerErrorUserNotFound())).build();
        }

        UserEntity user = list.get(0);
        List<ArrangementGuestEntity> guestEntityList = services.getAllComments(user);

        return Response.ok().entity(gson.toJson(guestEntityList)).build();
    }
}
